package com.example.storageapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "items")
data class StorageItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val imagePath: String? = null,
    val location: String,
    val category: String,
    val expiryDate: Long? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

// 预置位置
val PRESET_LOCATIONS = listOf("厨房", "卧室", "书房", "客厅", "储物间")

// 预置分类
val PRESET_CATEGORIES = listOf("食品", "药品", "日用品", "证件", "工具", "其他")

// 分类图标映射
val CATEGORY_ICONS = mapOf(
    "食品" to "🥛",
    "药品" to "💊",
    "日用品" to "🧴",
    "证件" to "📄",
    "工具" to "🔧",
    "其他" to "📦"
)

// 到期状态
enum class ExpiryStatus {
    EXPIRED,      // 已过期
    CRITICAL,     // 7天内到期
    WARNING,      // 30天内到期
    SAFE          // 安全
}

fun StorageItem.getExpiryStatus(): ExpiryStatus {
    if (expiryDate == null) return ExpiryStatus.SAFE
    
    val now = System.currentTimeMillis()
    val daysRemaining = ((expiryDate - now) / (1000 * 60 * 60 * 24)).toInt()
    
    return when {
        daysRemaining < 0 -> ExpiryStatus.EXPIRED
        daysRemaining <= 7 -> ExpiryStatus.CRITICAL
        daysRemaining <= 30 -> ExpiryStatus.WARNING
        else -> ExpiryStatus.SAFE
    }
}

fun StorageItem.getDaysRemaining(): Int? {
    if (expiryDate == null) return null
    val now = System.currentTimeMillis()
    return ((expiryDate - now) / (1000 * 60 * 60 * 24)).toInt()
}
