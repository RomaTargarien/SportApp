package com.example.sportapp.db

import androidx.room.TypeConverter
import com.example.sportapp.data.rss.materials.Enclosure

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
    }
}