package com.example.storageapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.storageapp.data.*
import com.example.storageapp.ui.components.*
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToBrowse: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToLocation: (String) -> Unit,
    viewModel: StorageViewModel = viewModel()
) {
    val stats by viewModel.stats.collectAsState()
    val expiringItems by viewModel.expiringItems.collectAsState()
    val recentItems by viewModel.recentItems.collectAsState()
    
    // 获取问候语
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 0..11 -> "早上好，收纳家"
        in 12..17 -> "下午好，收纳家"
        else -> "晚上好，收纳家"
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("收纳管家") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceLight
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = PrimaryOrange,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加物品")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 问候语
            Text(
                text = greeting,
                style = MaterialTheme.typography.headlineLarge
            )
            
            // 概览卡片
            IOSCard(
                elevation = 2
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatCard(
                            title = "总物品",
                            value = stats.totalItems.toString(),
                            icon = "📦",
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "存放位置",
                            value = stats.locationCount.toString(),
                            icon = "🏠",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    if (stats.expiringCount > 0) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = WarningYellow
                            )
                            Text(
                                text = "${stats.expiringCount} 件物品即将到期",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
            
            // 临期提醒模块
            if (expiringItems.isNotEmpty()) {
                IOSCard(
                    elevation = 2
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "临期提醒",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        expiringItems.take(5).forEach { item ->
                            ExpiringItemRow(
                                item = item,
                                onClick = { onNavigateToDetail(item.id) }
                            )
                        }
                        
                        TextButton(
                            onClick = onNavigateToBrowse,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "查看全部",
                                color = PrimaryOrange,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
            
            // 最近登记模块
            IOSCard(
                elevation = 2
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "最近登记",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    if (recentItems.isNotEmpty()) {
                        recentItems.forEach { item ->
                            RecentItemRow(
                                item = item,
                                onClick = { onNavigateToDetail(item.id) }
                            )
                        }
                    } else {
                        Text(
                            text = "暂无登记物品",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
            
            // 常用位置快捷入口
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "快捷位置",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PRESET_LOCATIONS.forEach { location ->
                        LocationQuickButton(
                            location = location,
                            onClick = { onNavigateToLocation(location) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpiringItemRow(
    item: StorageItem,
    onClick: () -> Unit
) {
    val daysRemaining = item.getDaysRemaining()
    val status = item.getExpiryStatus()
    val icon = CATEGORY_ICONS[item.category] ?: "📦"
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 24.sp)
            
            Column {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = item.location,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
        
        ExpiryIndicator(
            status = status,
            daysRemaining = daysRemaining
        )
    }
}

@Composable
private fun RecentItemRow(
    item: StorageItem,
    onClick: () -> Unit
) {
    val status = item.getExpiryStatus()
    val icon = CATEGORY_ICONS[item.category] ?: "📦"
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 24.sp)
            
            Column {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = item.location,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item.imagePath != null) {
                Icon(
                    imageVector = Icons.Outlined.CameraAlt,
                    contentDescription = "有图片",
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
            
            if (status != ExpiryStatus.SAFE) {
                val daysRemaining = item.getDaysRemaining()
                ExpiryIndicator(
                    status = status,
                    daysRemaining = daysRemaining
                )
            }
        }
    }
}

@Composable
private fun LocationQuickButton(
    location: String,
    onClick: () -> Unit
) {
    val icon = when (location) {
        "厨房" -> "🍳"
        "卧室" -> "🛏️"
        "书房" -> "📚"
        "客厅" -> "🛋️"
        "储物间" -> "📦"
        else -> "📍"
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            modifier = Modifier.size(60.dp),
            shape = CircleShape,
            color = SurfaceLight,
            shadowElevation = 2.dp
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 28.sp)
            }
        }
        
        Text(
            text = location,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}
