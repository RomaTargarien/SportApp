package com.example.sportapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sportapp.data.rss.materials.Item

@Dao
interface NewsDao {

    @Query("SELECT * FROM items")
    fun getAllNews(): List<Item>

    @Insert
    fun putNewItems(items: List<Item>)
}