package com.example.sportapp.models.rss.materials

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sportapp.db.DateConverter
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import java.util.*

@Xml
@Entity(tableName = "items")
data class Item(
    @PropertyElement val title: String,
    @PropertyElement val link: String,
    @PropertyElement val description: String,
    @PropertyElement(name = "pubDate",converter = DateConverter::class) val published: Date,
    @Element(name = "media:content") val enclosure: Enclosure,
    @PropertyElement val category: String,
    @PrimaryKey
    @PropertyElement val guid: String
) {
}