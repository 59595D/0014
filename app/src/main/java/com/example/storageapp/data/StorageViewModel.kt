package com.example.storageapp.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class StorageViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = StorageDatabase.getDatabase(application).storageItemDao()
    
    // 所有物品
    val allItems: StateFlow<List<StorageItem>> = dao.getAllItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // 最近添加的物品
    val recentItems: StateFlow<List<StorageItem>> = dao.getRecentItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // 统计数据
    private val _stats = MutableStateFlow(StorageStats(0, 0, 0))
    val stats: StateFlow<StorageStats> = _stats.asStateFlow()
    
    // 临期物品
    private val _expiringItems = MutableStateFlow<List<StorageItem>>(emptyList())
    val expiringItems: StateFlow<List<StorageItem>> = _expiringItems.asStateFlow()
    
    // 搜索结果
    private val _searchQuery = MutableStateFlow("")
    val searchResults: StateFlow<List<StorageItem>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flowOf(emptyList())
            } else {
                dao.searchItems(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    init {
        loadStats()
        loadExpiringItems()
    }
    
    private fun loadStats() {
        viewModelScope.launch {
            val total = dao.getTotalItemCount()
            val locations = dao.getLocationCount()
            val now = System.currentTimeMillis()
            val thirtyDaysLater = now + (30L * 24 * 60 * 60 * 1000)
            val expiring = dao.getExpiringCount(now, thirtyDaysLater)
            _stats.value = StorageStats(total, locations, expiring)
        }
    }
    
    private fun loadExpiringItems() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val thirtyDaysLater = now + (30L * 24 * 60 * 60 * 1000)
            dao.getExpiringItems(now, thirtyDaysLater).collect { items ->
                _expiringItems.value = items
            }
        }
    }
    
    fun searchItems(query: String) {
        _searchQuery.value = query
    }
    
    fun addItem(item: StorageItem) {
        viewModelScope.launch {
            dao.insertItem(item)
            loadStats()
        }
    }
    
    fun updateItem(item: StorageItem) {
        viewModelScope.launch {
            dao.updateItem(item)
            loadStats()
        }
    }
    
    fun deleteItem(item: StorageItem) {
        viewModelScope.launch {
            dao.deleteItem(item)
            loadStats()
        }
    }
    
    fun getItemsByLocation(location: String): Flow<List<StorageItem>> {
        return dao.getItemsByLocation(location)
    }
    
    fun getItemsByCategory(category: String): Flow<List<StorageItem>> {
        return dao.getItemsByCategory(category)
    }
    
    suspend fun getItemById(id: Long): StorageItem? {
        return dao.getItemById(id)
    }
    
    fun getAllLocations(): Flow<List<String>> {
        return dao.getAllLocations()
    }
    
    fun getAllCategories(): Flow<List<String>> {
        return dao.getAllCategories()
    }
    
    fun cleanup() {
        // 清理资源
    }
}

data class StorageStats(
    val totalItems: Int,
    val locationCount: Int,
    val expiringCount: Int
)
