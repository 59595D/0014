package com.example.storageapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import androidx.window.core.layout.calculateWindowSizeClass
import com.example.storageapp.data.StorageViewModel
import com.example.storageapp.ui.screens.*
import com.example.storageapp.ui.theme.*
import com.example.storageapp.utils.SpeechRecognitionHelper

class MainActivity : ComponentActivity() {
    
    private val viewModel: StorageViewModel by viewModels()
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "录音权限已授予", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "需要录音权限才能使用语音输入", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            StorageAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundLight
                ) {
                    val windowSizeClass = calculateWindowSizeClass(this)
                    val context = LocalContext.current
                    
                    // 提供权限请求函数
                    val requestPermission = {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                    
                    NavigationGraph(
                        windowWidthClass = windowSizeClass,
                        viewModel = viewModel,
                        requestPermission = requestPermission
                    )
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // 清理语音识别资源
        viewModel.cleanup()
    }
}

@Composable
fun NavigationGraph(
    windowWidthClass: WindowWidthSizeClass,
    viewModel: StorageViewModel,
    requestPermission: () -> Unit
) {
    val navController = rememberNavController()
    
    // 处理返回键
    BackHandler(enabled = true) {
        navController.navigateUp()
    }
    
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        // 首页
        composable("home") {
            HomeScreen(
                onNavigateToBrowse = {
                    navController.navigate("browse")
                },
                onNavigateToAdd = {
                    navController.navigate("add")
                },
                onNavigateToDetail = { itemId ->
                    navController.navigate("detail/$itemId")
                },
                onNavigateToLocation = { location ->
                    navController.navigate("browse")
                }
            )
        }
        
        // 添加物品页面
        composable("add") {
            AddItemScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }
        
        // 物品详情页
        composable(
            route = "detail/{itemId}",
            arguments = listOf(navArgument("itemId") { type = NavType.LongType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getLong("itemId") ?: return@composable
            DetailScreen(
                itemId = itemId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEdit = { editItemId ->
                    navController.navigate("edit/$editItemId")
                },
                viewModel = viewModel
            )
        }
        
        // 编辑物品页面（复用添加页面）
        composable(
            route = "edit/{itemId}",
            arguments = listOf(navArgument("itemId") { type = NavType.LongType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getLong("itemId") ?: return@composable
            EditItemScreen(
                itemId = itemId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }
        
        // 浏览页面
        composable(
            route = "browse?location={location}",
            arguments = listOf(
                navArgument("location") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val location = backStackEntry.arguments?.getString("location")
            BrowseScreen(
                windowWidthClass = windowWidthClass,
                onNavigateToDetail = { itemId ->
                    navController.navigate("detail/$itemId")
                },
                onNavigateBack = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                },
                viewModel = viewModel
            )
        }
    }
}
