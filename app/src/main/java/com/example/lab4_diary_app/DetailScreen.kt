package com.example.lab4_diary_app

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.lab4_diary_app.data.AppRepository
import com.example.lab4_diary_app.permissions.CameraPermissionScreen
import com.example.lab4_diary_app.permissions.LocationPermissionScreen
import com.example.lab4_diary_app.utils.formatTime
import com.example.lab4_diary_app.viewmodel.DetailState
import com.example.lab4_diary_app.viewmodel.DetailViewModel
import com.example.lab4_diary_app.viewmodel.DetailViewModelFactory
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

@Composable
fun DetailScreen(itemId: String, repository: AppRepository, modifier: Modifier = Modifier) {
    val viewModel: DetailViewModel = viewModel(
        factory = DetailViewModelFactory(repository, itemId)
    )

    val state by viewModel.state.collectAsStateWithLifecycle()

    var expanded by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(500)
    )

    val headerColor by animateColorAsState(
        targetValue = if (expanded)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(500)
    )

    when (state) {
        DetailState.Loading -> {
            Column(
                modifier = Modifier
                    .padding(32.dp),
            ) {
                CircularProgressIndicator()
                Text(
                    "Завантаження...",
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }

        is DetailState.Error -> {
            val message = (state as DetailState.Error).message

            Column(Modifier.padding(16.dp)) {
                Text(
                    "Помилка: $message",
                    color = MaterialTheme.colorScheme.error
                )

                Text(
                    "Спробуйте ще раз",
                    modifier = Modifier
                        .padding(top = 12.dp)
                )

                Button(
                    onClick = { viewModel.retry() },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Retry")
                }
            }
        }

        is DetailState.Success -> {
            val item = (state as DetailState.Success).item
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                val context = LocalContext.current
                val activity = context as android.app.Activity
                var photoUri by remember { mutableStateOf<String?>(null) }

                val cameraLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.TakePicture()
                ) { success ->
                    if (success && photoUri != null) {
                        viewModel.onPhotoTaken(photoUri!!)
                    }
                }

                val fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(context)

                val locationSettingsClient = LocationServices.getSettingsClient(context)


                item.photoUri?.let { uri ->
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        androidx.compose.foundation.Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = null,
                            modifier = Modifier
                                .height(200.dp)
                        )
                    }
                }

                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                CameraPermissionScreen {
                    Button(
                        onClick = {
                            val file = repository.createImageFile(context)

                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.provider",
                                file
                            )
                            photoUri = file.absolutePath
                            cameraLauncher.launch(uri)
                        },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Зробити фото")
                    }
                }


                LocationPermissionScreen {
                    Button(
                        onClick = {
                            val locationRequest = LocationRequest.Builder(
                                Priority.PRIORITY_HIGH_ACCURACY, 1000
                            ).build()

                            val settingsRequest = LocationSettingsRequest.Builder()
                                .addLocationRequest(locationRequest)
                                .build()

                            locationSettingsClient.checkLocationSettings(settingsRequest)
                                .addOnSuccessListener {
                                    try {
                                        fusedLocationClient.getCurrentLocation(
                                            Priority.PRIORITY_HIGH_ACCURACY, null
                                        ).addOnSuccessListener { location ->
                                            if (location != null) {
                                                viewModel.onLocationTaken(
                                                    lat = location.latitude,
                                                    lon = location.longitude,
                                                    accuracy = location.accuracy,
                                                    time = System.currentTimeMillis()
                                                )
                                            }
                                        }
                                    } catch (e: SecurityException) {
                                        Log.d("LOCATION", "SecurityException: ${e.message}")
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    if (exception is ResolvableApiException) {
                                        try {
                                            exception.startResolutionForResult(activity, 1001)
                                        } catch (e: Exception) {
                                            Log.d("LOCATION", "startResolution error: ${e.message}")
                                        }
                                    }
                                }
                        }
                    ) {
                        Text("Отримати локацію")
                    }
                }

                item.latitude?.let {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Text("Широта: ${item.latitude}")
                        Text("Довгота: ${item.longitude}")
                        Text("Точність: ${item.accuracy} м")
                        Text("Час: ${formatTime(item.locationTime ?: 0L)}")

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Відстань до точки: ${viewModel.distance} м")
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                        .clickable { expanded = !expanded },
                    colors = CardDefaults.cardColors(containerColor = headerColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Додаткова інформація",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(Modifier.weight(1f))

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.rotate(rotation)
                        )
                    }
                }

                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically(tween(500)) + fadeIn(tween(500)),
                    exit = shrinkVertically(tween(500)) + fadeOut(tween(500))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Пріоритет: ${item.priority}")
                        Text("Категорія: ${item.category}")
                        Text("Настрій: ${item.mood}")
                        Text("Обране: ${if (item.isFavorite) "Так" else "Ні"}")
                    }
                }
            }
        }

    }

}