package com.example.storageapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.storageapp.data.*
import com.example.storageapp.ui.theme.*

// iOS风格卡片
@Composable
fun IOSCard(
    modifier: Modifier = Modifier,
    elevation: Int = 2,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceLight
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation.dp
        ),
        content = content
    )
}

// iOS风格按钮
@Composable
fun IOSButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.Primary
) {
    when (variant) {
        ButtonVariant.Primary -> {
            Button(
                onClick = onClick,
                modifier = modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = enabled,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryOrange,
                    disabledContainerColor = PrimaryOrange.copy(alpha = 0.5f),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        ButtonVariant.Secondary -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PrimaryOrange
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = null,
                    width = 1.dp,
                    color = PrimaryOrange
                )
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        ButtonVariant.Danger -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ErrorRed
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = null,
                    width = 1.dp,
                    color = ErrorRed
                )
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

enum class ButtonVariant {
    Primary,
    Secondary,
    Danger
}

// 带语音输入的输入框
@Composable
fun VoiceInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    onVoiceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = TextTertiary) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = SurfaceLight,
            unfocusedContainerColor = SurfaceLight,
            focusedIndicatorColor = PrimaryOrange,
            unfocusedIndicatorColor = BorderColor,
            cursorColor = PrimaryOrange
        ),
        trailingIcon = {
            IconButton(onClick = onVoiceClick) {
                Icon(
                    imageVector = Icons.Outlined.Mic,
                    contentDescription = "语音输入",
                    tint = PrimaryOrange
                )
            }
        }
    )
}

// 图片上传区域
@Composable
fun ImageUploadArea(
    imagePath: String?,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (imagePath == null) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    color = SurfaceLight,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable(onClick = onPickImage),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "+",
                    fontSize = 32.sp,
                    color = BorderColor,
                    fontWeight = FontWeight.Light
                )
                Text(
                    text = "添加图片",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
            }
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Image(
                painter = rememberAsyncImagePainter(imagePath),
                contentDescription = "物品图片",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            IconButton(
                onClick = onRemoveImage,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "删除图片",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// 下拉选择框
@Composable
fun IOSDropdownMenu(
    value: String,
    options: List<String>,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onCustomInput: (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box(modifier = modifier.fillMaxWidth()) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = { },
                readOnly = true,
                label = { Text(label, color = TextPrimary) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SurfaceLight,
                    unfocusedContainerColor = SurfaceLight,
                    focusedIndicatorColor = PrimaryOrange,
                    unfocusedIndicatorColor = BorderColor
                )
            )
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = SurfaceLight,
                shadowElevation = 8.dp
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = TextPrimary
                        )
                    )
                }
                
                if (onCustomInput != null) {
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { 
                            Text(
                                "自定义...",
                                color = PrimaryOrange,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        onClick = {
                            expanded = false
                            onCustomInput()
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = PrimaryOrange
                        )
                    )
                }
            }
        }
    }
}

// 快捷标签选择器
@Composable
fun QuickTagSelector(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            FilterChip(
                selected = option == selected,
                onClick = { onSelect(option) },
                label = { Text(option, fontSize = 13.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryOrange,
                    selectedLabelColor = Color.White,
                    containerColor = SurfaceLight
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = BorderColor,
                    enabledBorderColor = BorderColor,
                    selectedBorderColor = PrimaryOrange
                ),
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

// 搜索框
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "搜索物品...",
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(placeholder) },
        modifier = modifier.fillMaxWidth(),
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "搜索",
                tint = TextSecondary
            )
        },
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = SurfaceLight,
            unfocusedContainerColor = SurfaceLight,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}

// 物品列表项
@Composable
fun ItemListItem(
    item: StorageItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val expiryStatus = item.getExpiryStatus()
    val daysRemaining = item.getDaysRemaining()
    val icon = CATEGORY_ICONS[item.category] ?: "📦"
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceLight
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 图标
            Text(
                text = icon,
                fontSize = 28.sp,
                modifier = Modifier.size(40.dp)
            )
            
            // 信息
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (item.imagePath != null) {
                        Icon(
                            imageVector = Icons.Outlined.CameraAlt,
                            contentDescription = "有图片",
                            tint = TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = item.location,
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    
                    if (expiryStatus != ExpiryStatus.SAFE && daysRemaining != null) {
                        val (dotColor, daysText) = when (expiryStatus) {
                            ExpiryStatus.EXPIRED -> ErrorRed to "已过期"
                            ExpiryStatus.CRITICAL -> ErrorRed to "剩${daysRemaining}天"
                            ExpiryStatus.WARNING -> WarningYellow to "剩${daysRemaining}天"
                            ExpiryStatus.SAFE -> TextSecondary to ""
                        }
                        
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(dotColor, CircleShape)
                        )
                        
                        Text(
                            text = daysText,
                            style = MaterialTheme.typography.labelSmall,
                            color = dotColor
                        )
                    }
                }
            }
        }
    }
}

// 到期状态指示器
@Composable
fun ExpiryIndicator(
    status: ExpiryStatus,
    daysRemaining: Int?,
    modifier: Modifier = Modifier
) {
    if (status == ExpiryStatus.SAFE || daysRemaining == null) return
    
    val color = when (status) {
        ExpiryStatus.EXPIRED, ExpiryStatus.CRITICAL -> ErrorRed
        ExpiryStatus.WARNING -> WarningYellow
        ExpiryStatus.SAFE -> return
    }
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Text(
            text = if (daysRemaining < 0) "已过期" else "剩${daysRemaining}天",
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

// 统计卡片
@Composable
fun StatCard(
    title: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceLight
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                fontSize = 36.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = PrimaryOrange
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
        }
    }
}


