package com.example.sportapp.models.rss.materials

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml
data class Rss(
    @Attribute val version: Double,
    @Attribute(name = "xmlns:content") val content: String,
    @Element val channel: Channel
)
