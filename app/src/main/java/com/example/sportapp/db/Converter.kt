package com.example.sportapp.db

import androidx.room.TypeConverter
import com.example.sportapp.models.rss.materials.Enclosure
import java.util.Date

class Converter {
    companion object {
        @TypeConverter
        @JvmStatic
        fun fromEnclosure(enclosure: Enclosure): String {
            return "${enclosure.url}|${enclosure.length}|${enclosure.type}"
        }
        @TypeConverter
        @JvmStatic
        fun toEnclosure(string: String): Enclosure {
            val list = string.split("|")
            return Enclosure(list[0],list[1].toInt(),list[2])
        }
        @TypeConverter
        @JvmStatic
        fun toDate(value: Long): Date {
            return Date(value)
        }
        @TypeConverter
        @JvmStatic
        fun fromDate(date: Date): Long {
            return date.time
        }
    }
}