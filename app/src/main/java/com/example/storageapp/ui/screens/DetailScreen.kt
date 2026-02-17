package com.example.storageapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.storageapp.data.*
import com.example.storageapp.ui.components.*
import com.example.storageapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    itemId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: StorageViewModel = viewModel()
) {
    var item by remember { mutableStateOf<StorageItem?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    LaunchedEffect(itemId) {
        viewModel.getItemById(itemId)?.let {
            item = it
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("物品详情") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceLight
                )
            )
        }
    ) { paddingValues ->
        item?.let { currentItem ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 大图标
                val icon = CATEGORY_ICONS[currentItem.category] ?: "📦"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = icon,
                        fontSize = 100.sp
                    )
                }
                
                // 物品图片
                if (currentItem.imagePath != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(currentItem.imagePath),
                            contentDescription = "物品图片",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // 信息卡片
                IOSCard(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    elevation = 2
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // 名称
                        Text(
                            text = currentItem.name,
                            style = MaterialTheme.typography.headlineLarge
                        )
                        
                        HorizontalDivider(color = DividerColor)
                        
                        // 基本信息
                        InfoRow(label = "存放位置", value = currentItem.location)
                        InfoRow(label = "分类", value = currentItem.category)
                        
                        // 保质期
                        if (currentItem.expiryDate != null) {
                            val expiryStatus = currentItem.getExpiryStatus()
                            val daysRemaining = currentItem.getDaysRemaining()
                            val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
                            val expiryText = dateFormat.format(Date(currentItem.expiryDate))
                            
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "保质期",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextSecondary
                                )
                                
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = expiryText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = when (expiryStatus) {
                                            ExpiryStatus.EXPIRED, ExpiryStatus.CRITICAL -> ErrorRed
                                            ExpiryStatus.WARNING -> WarningYellow
                                            ExpiryStatus.SAFE -> TextPrimary
                                        }
                                    )
                                    
                                    ExpiryIndicator(
                                        status = expiryStatus,
                                        daysRemaining = daysRemaining
                                    )
                                }
                            }
                        } else {
                            InfoRow(label = "保质期", value = "无保质期")
                        }
                        
                        // 备注
                        if (!currentItem.notes.isNullOrBlank()) {
                            HorizontalDivider(color = DividerColor)
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "备注",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextSecondary
                                )
                                Text(
                                    text = currentItem.notes,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
                
                // 按钮组
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IOSButton(
                        text = "编辑",
                        onClick = { onNavigateToEdit(currentItem.id) },
                        variant = ButtonVariant.Secondary,
                        modifier = Modifier.weight(1f)
                    )
                    
                    IOSButton(
                        text = "删除",
                        onClick = { showDeleteDialog = true },
                        variant = ButtonVariant.Danger,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryOrange)
            }
        }
    }
    
    // 删除确认对话框
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除这个物品吗？此操作无法撤销。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        item?.let {
                            viewModel.deleteItem(it)
                            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show()
                        }
                        showDeleteDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("删除", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
