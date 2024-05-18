package com.example.news

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.news.ui.theme.NewsTheme
import com.example.news.view.NewsArticleScreen
import com.example.news.view.NewsHomeScreen
import com.example.news.viewmodel.NewsViewModel
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        askNotificationPermission()

        val newsViewModel: NewsViewModel by viewModels()

        FirebaseMessaging.getInstance().subscribeToTopic("news")
            .addOnCompleteListener { task ->
                var msg = "Subscription successful"
                if (!task.isSuccessful) {
                    msg = "Subscription failed"
                }
                Log.d("FCM", msg)
            }

        setContent {
            NewsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val navController = rememberNavController()

                    NavHost(navController, startDestination = Screen.NewsHomeScreen.route) {

                        composable(Screen.NewsHomeScreen.route) {
                            NewsHomeScreen(
                                navController = navController,
                                viewModel = newsViewModel
                            )
                        }
                        composable(
                            route = "${Screen.NewsArticleScreen.route}/{url}",
                            arguments = listOf(
                                navArgument("url"){type = NavType.StringType},)
                        ){backStackEntry->

                            NewsArticleScreen(navController = navController)

                        }
                    }
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    val permissionsToRequest = arrayOf(
        POST_NOTIFICATIONS
    )

    private fun requestPermissions(
        permissions: Array<String>,
        launcher: ActivityResultLauncher<Array<String>>
    ) {
        val permissionsToRequest = permissions.filter { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
        if (permissionsToRequest.isNotEmpty()) {
            launcher.launch(permissionsToRequest.toTypedArray())
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    val permissionResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                Toast.makeText(this, "Notification permissions are Granted.", Toast.LENGTH_SHORT).show()
            } else {
                val deniedPermissions = permissions.filter { !it.value }
                if (deniedPermissions.any { !shouldShowRequestPermissionRationale(it.key) }) {
                    showAppSettingsDialog()
                } else {
                    Toast.makeText(this, "Notification permissions are required.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                Log.e("Main Activity", "PERMISSION_GRANTED")
            } else {
                requestPermissions(permissionsToRequest, permissionResultLauncher)
            }
        }
    }

    private fun showAppSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permissions required")
            .setMessage("Please grant access to notification in App Settings to continue.")
            .setPositiveButton("Go to Settings") { _, _ ->
                navigateToAppSettings()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun navigateToAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}

