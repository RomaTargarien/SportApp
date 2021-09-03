package com.example.sportapp.models.rss.materials

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml
data class Image(
    @PropertyElement val url: String,
    @PropertyElement val link: String,
    @PropertyElement val title: String
)
