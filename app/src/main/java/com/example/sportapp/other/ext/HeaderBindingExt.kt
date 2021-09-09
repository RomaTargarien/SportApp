package com.example.sportapp.other.ext

import android.view.View
import com.bumptech.glide.Glide
import com.example.sportapp.databinding.ItemMaterialHeaderBinding
import com.example.sportapp.models.rss.materials.Item
import java.text.SimpleDateFormat

fun ItemMaterialHeaderBinding.display(item:Item,view: View,dateFormatter: SimpleDateFormat) {
    Glide.with(view.context)
        .load(item.enclosure.url)
        .into(this.ivHeader)
    this.tvHeaderTittle.text = item.title
    this.tvHeaderDate.text = dateFormatter.format(item.published)
    this.tvHeaderCategory.text = item.category
}