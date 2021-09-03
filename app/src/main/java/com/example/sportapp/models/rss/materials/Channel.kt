package com.example.sportapp.models.rss.materials

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml


@Xml
data class Channel(
    @PropertyElement val title: String,
    @PropertyElement val link: String,
    @PropertyElement val description: String? = "",
    @Element val image: Image,
    @Element val items: List<Item>
    )