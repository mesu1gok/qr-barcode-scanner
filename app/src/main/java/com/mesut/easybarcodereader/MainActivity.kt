/*
 * Copyright 2026 Mesut Gök
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mesut.easybarcodereader

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.room.*
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

//DATABASE
@Entity(tableName = "scans")
data class ScanResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val content: String,
    val isFavorite: Boolean = false
)

@Dao
interface ScanDao {
    @Query("SELECT * FROM scans ORDER BY id DESC") // Fetch all scan results in descending order
    fun getAll(): Flow<List<ScanResult>>

    @Query("SELECT * FROM scans WHERE isFavorite = 1 ORDER BY id DESC") // Fetch only favorite scans
    fun getFavorites(): Flow<List<ScanResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE) // Insert new scan result or replace if exists
    suspend fun insert(scan: ScanResult)

    @Query("UPDATE scans SET isFavorite = :isFav WHERE id = :id") // Update favorite status of a scan
    suspend fun updateFavorite(id: Int, isFav: Boolean)

    @Query("DELETE FROM scans") // Delete all scans
    suspend fun deleteAll()
}

@Database(entities = [ScanResult::class], version = 2) // Database versioning
abstract class AppDatabase : RoomDatabase() {
    abstract fun scanDao(): ScanDao
}

class MainActivity : ComponentActivity() {
    private val db by lazy {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "easy_qr_db")
            .fallbackToDestructiveMigration() // Handle migrations destructively
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen() // Show splash screen on app launch
        super.onCreate(savedInstanceState)
        setContent {
            var currentScreen by remember { mutableStateOf("Scan") } // Track the current screen
            val scanHistory by db.scanDao().getAll().collectAsState(initial = emptyList()) // Collect scan history
            val favorites by db.scanDao().getFavorites().collectAsState(initial = emptyList()) // Collect favorite scans
            val scope = rememberCoroutineScope() // Coroutine scope for database operations

            MaterialTheme(colorScheme = lightColorScheme(primary = Color.Black)) {
                Scaffold(
                    bottomBar = {
                        NavigationBar(containerColor = Color.White) {
                            NavigationBarItem(
                                selected = currentScreen == "Scan",
                                onClick = { currentScreen = "Scan" }, // Switch to Scan screen
                                icon = { Icon(Icons.Default.QrCodeScanner, null) },
                                label = { Text("Scan") }
                            )
                            NavigationBarItem(
                                selected = currentScreen == "History",
                                onClick = { currentScreen = "History" }, // Switch to History screen
                                icon = { Icon(Icons.Default.History, null) },
                                label = { Text("History") }
                            )
                            NavigationBarItem(
                                selected = currentScreen == "Favorites",
                                onClick = { currentScreen = "Favorites" }, // Switch to Favorites screen
                                icon = { Icon(if(currentScreen == "Favorites") Icons.Default.Favorite else Icons.Outlined.FavoriteBorder, null) },
                                label = { Text("Favorites") }
                            )
                        }
                    }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        when (currentScreen) {
                            "Scan" -> CameraPermissionWrapper { result -> // Handle scanned result
                                scope.launch(Dispatchers.IO) {
                                    db.scanDao().insert(ScanResult(content = result)) // Insert scan result into database
                                }
                            }
                            "History" -> ListScreen("History", scanHistory, db.scanDao(), scope)
                            "Favorites" -> ListScreen("Favorites", favorites, db.scanDao(), scope)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResultActions(content: String, isFavorite: Boolean = false, onFavClick: (() -> Unit)? = null) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val clipboard = LocalClipboardManager.current

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Result:", fontWeight = FontWeight.Bold)
            if (onFavClick != null) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isFavorite) Color.Red else Color.Gray,
                    modifier = Modifier.size(24.dp).clickable { onFavClick() } // Favorite icon with click listener
                )
            }
        }
        Text(content, color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp), maxLines = 2, overflow = TextOverflow.Ellipsis) // Display scanned content
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { shareText(context, content) }, modifier = Modifier.weight(1f)) {
                Text("Share") // Button to share the result
            }
            if (content.startsWith("http")) {
                Button(onClick = { try { uriHandler.openUri(content) } catch (e: Exception) {} }, modifier = Modifier.weight(1f)) {
                    Text("Go to Site") // Button to open URL in browser
                }
            } else {
                Button(onClick = {
                    clipboard.setText(AnnotatedString(content)) // Copy content to clipboard
                    Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show() // Show copied message
                }, modifier = Modifier.weight(1f)) {
                    Text("Copy")
                }
            }
        }
    }
}

@Composable
fun ListScreen(title: String, list: List<ScanResult>, dao: ScanDao, scope: kotlinx.coroutines.CoroutineScope) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
        if (list.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Empty", color = Color.Gray) }
        } else {
            LazyColumn {
                items(list) { item ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                        ResultActions(item.content, item.isFavorite) {
                            scope.launch(Dispatchers.IO) { dao.updateFavorite(item.id, !item.isFavorite) }
                        }
                    }
                }
            }
        }
    }
}

fun shareText(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share"))
}

@Composable
fun CameraPermissionWrapper(onResultFound: (String) -> Unit) {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { hasPermission = it }
    LaunchedEffect(Unit) { if (!hasPermission) launcher.launch(Manifest.permission.CAMERA) } // Request camera permission
    if (hasPermission) QRScannerScreen(onResultFound)
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun QRScannerScreen(onResultFound: (String) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var lastScanned by remember { mutableStateOf("") }
    var showPopup by remember { mutableStateOf(false) }
    var popupContent by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { ctx ->
            val previewView = PreviewView(ctx) // Create CameraX preview view
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx) // Get camera provider
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) } // Setup camera preview
                val analysis = ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build() // Setup image analysis
                analysis.setAnalyzer(Executors.newSingleThreadExecutor()) { proxy -> // Set image analyzer
                    proxy.image?.let { img ->
                        BarcodeScanning.getClient().process(InputImage.fromMediaImage(img, proxy.imageInfo.rotationDegrees))
                            .addOnSuccessListener { barcodes -> // Process scanned barcodes
                                barcodes.firstOrNull()?.rawValue?.let { result -> // Get first barcode result
                                    if (result != lastScanned) { // Avoid duplicate scans
                                        lastScanned = result
                                        popupContent = result
                                        showPopup = true
                                        onResultFound(result) // Invoke callback with scanned result
                                    }
                                }
                            }.addOnCompleteListener { proxy.close() } // Close image proxy after processing
                    }
                }
                cameraProvider.unbindAll() // Unbind all use cases before binding new ones
                cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis) // Bind camera use cases to lifecycle
            }, ContextCompat.getMainExecutor(ctx))
            previewView // Return the preview view
        }, modifier = Modifier.fillMaxSize())

        if (showPopup) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                Card(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.98f)),
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {
                    Column {
                        ResultActions(popupContent) // Display scanned result actions
                        TextButton(onClick = { showPopup = false }, modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp)) {
                            Text("Scan Again", color = Color.Gray) // Button to scan again
                        }
                    }
                }
            }
        }
    }
}
