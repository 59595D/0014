package com.example.storageapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.core.layout.WindowWidthSizeClass
import androidx.window.core.layout.WindowSizeClass
import coil.compose.rememberAsyncImagePainter
import com.example.storageapp.data.*
import com.example.storageapp.ui.components.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BrowseScreen(
    windowWidthClass: WindowWidthSizeClass,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: StorageViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(BrowseTab.LOCATION) }
    var selectedItem by remember { mutableStateOf<StorageItem?>(null) }
    
    val allItems by viewModel.allItems.collectAsState()
    
    // 根据搜索和标签筛选物品
    val filteredItems = remember(searchQuery, selectedTab, allItems) {
        if (searchQuery.isBlank()) {
            allItems
        } else {
            allItems.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }
    
    // 按标签分组
    val groupedItems = remember(selectedTab, filteredItems) {
        when (selectedTab) {
            BrowseTab.LOCATION -> filteredItems.groupBy { it.location }
            BrowseTab.CATEGORY -> filteredItems.groupBy { it.category }
        }
    }
    
    // 紧凑模式: 显示列表,点击跳转详情页
    // 展开/中等模式: 左右分栏
    if (windowWidthClass == WindowWidthSizeClass.COMPACT) {
        // 紧凑模式
        BrowseListScreen(
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it },
            selectedTab = selectedTab,
            onTabChange = { selectedTab = it },
            groupedItems = groupedItems,
            onItemClick = { onNavigateToDetail(it) }
        )
    } else {
        // 展开模式: 左右分栏
        BrowseSplitScreen(
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it },
            selectedTab = selectedTab,
            onTabChange = { selectedTab = it },
            groupedItems = groupedItems,
            selectedItem = selectedItem,
            onItemClick = { selectedItem = it },
            onBack = onNavigateBack
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BrowseListScreen(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedTab: BrowseTab,
    onTabChange: (BrowseTab) -> Unit,
    groupedItems: Map<String, List<StorageItem>>,
    onItemClick: (Long) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("浏览物品") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 搜索框
            SearchBar(
                query = searchQuery,
                onQueryChange = onSearchChange
            )
            
            // Tab切换
            BrowseTabRow(
                selectedTab = selectedTab,
                onTabChange = onTabChange
            )
            
            // 物品列表
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (groupedItems.isEmpty()) {
                    item {
                        Text(
                            text = "暂无物品",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(32.dp)
                        )
                    }
                } else {
                    groupedItems.forEach { (groupName, items) ->
                        item {
                            GroupHeader(
                                name = groupName,
                                count = items.size
                            )
                        }
                        
                        items(items) { item ->
                            ItemListItem(
                                item = item,
                                onClick = { onItemClick(item.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BrowseSplitScreen(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedTab: BrowseTab,
    onTabChange: (BrowseTab) -> Unit,
    groupedItems: Map<String, List<StorageItem>>,
    selectedItem: StorageItem?,
    onItemClick: (StorageItem) -> Unit,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        // 左侧列表
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            // 列表顶部栏
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceLight)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "浏览物品",
                    style = MaterialTheme.typography.headlineMedium
                )
                
                SearchBar(
                    query = searchQuery,
                    onQueryChange = onSearchChange
                )
                
                BrowseTabRow(
                    selectedTab = selectedTab,
                    onTabChange = onTabChange
                )
            }
            
            // 列表内容
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (groupedItems.isEmpty()) {
                    item {
                        Text(
                            text = "暂无物品",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(32.dp)
                        )
                    }
                } else {
                    groupedItems.forEach { (groupName, items) ->
                        item {
                            GroupHeader(
                                name = groupName,
                                count = items.size
                            )
                        }
                        
                        items(items) { item ->
                            ItemListItem(
                                item = item,
                                onClick = { onItemClick(item) },
                                modifier = Modifier.background(
                                    if (selectedItem?.id == item.id) {
                                        PrimaryOrange.copy(alpha = 0.1f)
                                    } else {
                                        SurfaceLight
                                    }
                                )
                            )
                        }
                    }
                }
            }
        }
        
        // 右侧详情
        HorizontalDivider(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight(),
            color = DividerColor
        )
        
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(BackgroundLight)
        ) {
            if (selectedItem != null) {
                DetailPanelContent(
                    item = selectedItem,
                    onBack = onBack
                )
            } else {
                // 占位提示
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "请选择物品查看详情",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun BrowseTabRow(
    selectedTab: BrowseTab,
    onTabChange: (BrowseTab) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        BrowseTab.entries.forEach { tab ->
            BrowseTabItem(
                tab = tab,
                selected = tab == selectedTab,
                onClick = { onTabChange(tab) }
            )
        }
    }
    
    HorizontalDivider(
        modifier = Modifier.padding(top = 8.dp),
        color = DividerColor
    )
}

@Composable
private fun BrowseTabItem(
    tab: BrowseTab,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = tab.displayName,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) PrimaryOrange else TextSecondary
        )
        
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .width(20.dp)
                .height(2.dp)
                .background(
                    if (selected) PrimaryOrange else Color.Transparent
                )
        )
    }
}

@Composable
private fun GroupHeader(
    name: String,
    count: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        Text(
            text = "$count 件",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary
        )
    }
}

@Composable
private fun DetailPanelContent(
    item: StorageItem,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 顶部栏(在展开模式下隐藏返回按钮)
        TopAppBar(
            title = { Text("物品详情") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = SurfaceLight
            )
        )
        
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 大图标
            val icon = CATEGORY_ICONS[item.category] ?: "📦"
            Text(
                text = icon,
                fontSize = 80.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            
            // 物品图片
            if (item.imagePath != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    androidx.compose.foundation.Image(
                        painter = rememberAsyncImagePainter(item.imagePath),
                        contentDescription = "物品图片",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            
            // 信息卡片
            IOSCard {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    
                    InfoRow("存放位置", item.location)
                    InfoRow("分类", item.category)
                    
                    if (item.expiryDate != null) {
                        val expiryStatus = item.getExpiryStatus()
                        val daysRemaining = item.getDaysRemaining()
                        
                        Row(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "保质期",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondary
                            )
                            
                            val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                            Text(
                                text = dateFormat.format(java.util.Date(item.expiryDate)),
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
                    } else {
                        InfoRow("保质期", "无保质期")
                    }
                    
                    if (!item.notes.isNullOrBlank()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "备注",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondary
                            )
                            Text(
                                text = item.notes,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
            
            // 按钮组
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IOSButton(
                    text = "编辑",
                    onClick = { },
                    variant = ButtonVariant.Secondary,
                    modifier = Modifier.weight(1f)
                )
                
                IOSButton(
                    text = "删除",
                    onClick = { },
                    variant = ButtonVariant.Danger,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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

enum class BrowseTab(val displayName: String) {
    LOCATION("按位置"),
    CATEGORY("按分类")
}
