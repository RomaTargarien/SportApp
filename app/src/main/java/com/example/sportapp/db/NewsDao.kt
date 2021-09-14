package com.example.sportapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sportapp.models.rss.materials.Item
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

@Dao
interface NewsDao {

    @Query("SELECT * FROM items ORDER BY published DESC")
    fun getAllNews(): List<Item>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun putNewItems(items: List<Item>)

    @Query("DELETE FROM items")
    fun delete()

    @Query("SELECT * FROM items WHERE category LIKE :category2 ORDER BY published DESC LIMIT :limit OFFSET :offset")
    fun getItemsWithOffset(offset: Int,limit: Int = 10,category2: String): List<Item>


}