package com.example.lab4_diary_app.permissions

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun CameraPermissionScreen(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val activity = context as Activity

    var permissionState by remember { mutableStateOf<PermissionState>(PermissionState.Denied) }

    val lifecycleOwner = LocalLifecycleOwner.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        permissionState = if (granted) {
            PermissionState.Granted
        } else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.CAMERA
                )
            ) {
                PermissionState.PermanentlyDenied
            } else {
                PermissionState.Denied
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {

                val granted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                permissionState = if (granted) {
                    PermissionState.Granted
                } else {
                    PermissionState.Denied
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    when(permissionState) {
        PermissionState.Granted -> content()

        PermissionState.Denied -> {
            PermissionDeniedUI {
                launcher.launch(Manifest.permission.CAMERA)
            }
        }

        PermissionState.PermanentlyDenied -> {
            PermissionPermanentlyDeniedUI()
        }
    }
}

@Composable
private fun PermissionDeniedUI(onRequest: () -> Unit) {
    Column {
        Text("Застосунок потребує доступ до камери для створення фото елементів.")
        Button(onClick = onRequest) {
            Text("Надати дозвіл")
        }
    }
}

@Composable
private fun PermissionPermanentlyDeniedUI() {
    val context = LocalContext.current

    Column {
        Text("Дозвіл відхилено. Перейдіть у налаштування застосунку.")
        Button(
            onClick = {
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
                context.startActivity(intent)
            }
        ) {
            Text("Перейти в налаштування")
        }
    }
}