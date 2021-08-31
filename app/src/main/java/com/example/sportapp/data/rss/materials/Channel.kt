package com.example.sportapp.data.rss.materials

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import org.jetbrains.annotations.NotNull

@Xml
data class Rss(
    @Attribute val version: Double,
    @Attribute(name = "xmlns:content") val content: String,
    @Element val channel: Channel
    )

@Xml
data class Channel(
    @PropertyElement val title: String,
    @PropertyElement val link: String,
    @PropertyElement val description: String? = "",
    @Element val image: Image,
    @Element val items: List<Item>
    )

@Xml
data class Image(
    @PropertyElement val url: String,
    @PropertyElement val link: String,
    @PropertyElement val title: String
    )

@Xml
@Entity(tableName = "items")
data class Item(
    @PropertyElement val title: String,
    @PropertyElement val link: String,
    @PropertyElement val description: String,
    @PropertyElement(name = "pubDate") val published: String,
    @Element val enclosure: Enclosure,
    @PropertyElement val category: String,
    @PrimaryKey
    @PropertyElement val guid: String
    ) {
}

@Xml
data class Enclosure(
    @Attribute val url: String,
    @Attribute val length: Int,
    @Attribute val type: String)