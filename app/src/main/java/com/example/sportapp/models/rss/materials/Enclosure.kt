package com.example.sportapp.models.rss.materials

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml

@Xml
data class Enclosure(
    @Attribute val url: String,
    @Attribute val length: Int,
    @Attribute val type: String)
