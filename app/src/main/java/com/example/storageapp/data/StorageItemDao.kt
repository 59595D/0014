package com.example.storageapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StorageItemDao {
    @Query("SELECT * FROM items ORDER BY createdAt DESC")
    fun getAllItems(): Flow<List<StorageItem>>

    @Query("SELECT * FROM items WHERE id = :itemId")
    suspend fun getItemById(itemId: Long): StorageItem?

    @Query("SELECT * FROM items WHERE location = :location ORDER BY createdAt DESC")
    fun getItemsByLocation(location: String): Flow<List<StorageItem>>

    @Query("SELECT * FROM items WHERE category = :category ORDER BY createdAt DESC")
    fun getItemsByCategory(category: String): Flow<List<StorageItem>>

    @Query("SELECT * FROM items WHERE name LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchItems(query: String): Flow<List<StorageItem>>

    @Query("""
        SELECT * FROM items 
        WHERE expiryDate IS NOT NULL 
        AND expiryDate BETWEEN :startDate AND :endDate
        ORDER BY expiryDate ASC
        LIMIT 5
    """)
    fun getExpiringItems(startDate: Long, endDate: Long): Flow<List<StorageItem>>

    @Query("SELECT COUNT(*) FROM items")
    suspend fun getTotalItemCount(): Int

    @Query("SELECT COUNT(DISTINCT location) FROM items")
    suspend fun getLocationCount(): Int

    @Query("""
        SELECT COUNT(*) FROM items 
        WHERE expiryDate IS NOT NULL 
        AND expiryDate > :now 
        AND expiryDate <= :thirtyDaysLater
    """)
    suspend fun getExpiringCount(now: Long, thirtyDaysLater: Long): Int

    @Query("SELECT * FROM items ORDER BY createdAt DESC LIMIT 5")
    fun getRecentItems(): Flow<List<StorageItem>>

    @Insert
    suspend fun insertItem(item: StorageItem): Long

    @Update
    suspend fun updateItem(item: StorageItem)

    @Delete
    suspend fun deleteItem(item: StorageItem)

    @Query("SELECT DISTINCT location FROM items")
    fun getAllLocations(): Flow<List<String>>

    @Query("SELECT DISTINCT category FROM items")
    fun getAllCategories(): Flow<List<String>>
}
