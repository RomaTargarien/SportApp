package com.example.sportapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.sportapp.models.rss.materials.Item


@Database(entities = [Item::class],version = 2)
@TypeConverters(Converter::class)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun getNewsDao(): NewsDao
}