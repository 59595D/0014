package com.example.storageapp.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.storageapp.data.*
import com.example.storageapp.ui.components.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemScreen(
    itemId: Long,
    onNavigateBack: () -> Unit,
    viewModel: StorageViewModel = viewModel()
) {
    var item by remember { mutableStateOf<StorageItem?>(null) }
    
    var name by remember { mutableStateOf("") }
    var imagePath by remember { mutableStateOf<String?>(null) }
    var location by remember { mutableStateOf(PRESET_LOCATIONS[0]) }
    var category by remember { mutableStateOf(PRESET_CATEGORIES[0]) }
    var expiryDate by remember { mutableStateOf<Long?>(null) }
    var hasNoExpiry by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showCustomLocationDialog by remember { mutableStateOf(false) }
    var customLocationInput by remember { mutableStateOf("") }
    
    LaunchedEffect(itemId) {
        viewModel.getItemById(itemId)?.let {
            item = it
            name = it.name
            imagePath = it.imagePath
            location = it.location
            category = it.category
            expiryDate = it.expiryDate
            hasNoExpiry = it.expiryDate == null
            notes = it.notes ?: ""
        }
    }
    
    // 图片选择
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val contentResolver = context.contentResolver
                val inputStream = contentResolver.openInputStream(uri)
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val imageFile = File(context.externalCacheDir, "IMG_${timeStamp}.jpg")
                
                inputStream?.use { input ->
                    imageFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                
                imagePath = imageFile.absolutePath
            } catch (e: Exception) {
                Toast.makeText(context, "图片保存失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("编辑物品") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 物品名称
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("物品名称", color = TextTertiary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SurfaceLight,
                    unfocusedContainerColor = SurfaceLight,
                    focusedIndicatorColor = PrimaryOrange,
                    unfocusedIndicatorColor = BorderColor
                )
            )
            
            // 物品图片
            ImageUploadArea(
                imagePath = imagePath,
                onPickImage = {
                    imagePickerLauncher.launch("image/*")
                },
                onRemoveImage = {
                    imagePath = null
                }
            )
            
            // 存放位置
            Text(
                text = "存放位置",
                style = MaterialTheme.typography.labelMedium,
                color = TextPrimary
            )
            
            IOSDropdownMenu(
                value = location,
                options = PRESET_LOCATIONS,
                label = "选择位置",
                onValueChange = { location = it },
                onCustomInput = {
                    customLocationInput = ""
                    showCustomLocationDialog = true
                }
            )
            
            // 快捷位置标签
            QuickTagSelector(
                options = PRESET_LOCATIONS,
                selected = location,
                onSelect = { location = it }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 分类
            Text(
                text = "分类",
                style = MaterialTheme.typography.labelMedium,
                color = TextPrimary
            )
            
            IOSDropdownMenu(
                value = category,
                options = PRESET_CATEGORIES,
                label = "选择分类",
                onValueChange = { category = it }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 保质期
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "保质期/到期日",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Checkbox(
                        checked = hasNoExpiry,
                        onCheckedChange = { 
                            hasNoExpiry = it
                            if (it) expiryDate = null
                        },
                        colors = CheckboxDefaults.colors(checkedColor = PrimaryOrange)
                    )
                    Text(
                        text = "无保质期",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            
            if (!hasNoExpiry) {
                OutlinedTextField(
                    value = if (expiryDate != null) {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(Date(expiryDate!!))
                    } else {
                        ""
                    },
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("选择日期") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SurfaceLight,
                        unfocusedContainerColor = SurfaceLight,
                        focusedIndicatorColor = PrimaryOrange,
                        unfocusedIndicatorColor = BorderColor
                    ),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }, enabled = !hasNoExpiry) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "选择日期",
                                tint = if (!hasNoExpiry) PrimaryOrange else TextTertiary
                            )
                        }
                    },
                    enabled = !hasNoExpiry
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 备注
            Text(
                text = "备注",
                style = MaterialTheme.typography.labelMedium,
                color = TextPrimary
            )
            
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                placeholder = { Text("添加备注信息...", color = TextTertiary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SurfaceLight,
                    unfocusedContainerColor = SurfaceLight,
                    focusedIndicatorColor = PrimaryOrange,
                    unfocusedIndicatorColor = BorderColor
                ),
                maxLines = 5
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 保存按钮
            IOSButton(
                text = "保存修改",
                onClick = {
                    if (name.isBlank()) {
                        Toast.makeText(context, "请输入物品名称", Toast.LENGTH_SHORT).show()
                        return@IOSButton
                    }
                    
                    item?.let {
                        val updatedItem = it.copy(
                            name = name,
                            imagePath = imagePath,
                            location = location,
                            category = category,
                            expiryDate = expiryDate,
                            notes = notes.ifBlank { null }
                        )
                        
                        viewModel.updateItem(updatedItem)
                        Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show()
                        onNavigateBack()
                    }
                },
                enabled = name.isNotBlank()
            )
        }
    }
    
    // 日期选择器
    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
                date?.let {
                    expiryDate = it
                }
                showDatePicker = false
            },
            onDismiss = {
                showDatePicker = false
            }
        )
    }
    
    // 自定义位置对话框
    if (showCustomLocationDialog) {
        AlertDialog(
            onDismissRequest = { showCustomLocationDialog = false },
            title = { Text("自定义位置") },
            text = {
                OutlinedTextField(
                    value = customLocationInput,
                    onValueChange = { customLocationInput = it },
                    placeholder = { Text("输入位置名称") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (customLocationInput.isNotBlank()) {
                            location = customLocationInput
                            showCustomLocationDialog = false
                        }
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomLocationDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}
