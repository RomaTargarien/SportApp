package com.example.sportapp.other.ext

import android.view.View
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.sportapp.databinding.ItemMaterialBinding
import com.example.sportapp.models.rss.materials.Item
import com.example.sportapp.other.toDp
import java.text.SimpleDateFormat

fun ItemMaterialBinding.display(item: Item,view: View,position: Int,formatter: SimpleDateFormat) {
    val params = this.itemMaterialConstraint.layoutParams
    params.height = 130.toDp(view.context)
    if (position == 1 || position % 7 == 1) {
        params.height = 173.toDp(view.context)
        this.cardCategory.isVisible = true
        this.tvCategory.text = "Смотрите больше новостей по теме ${item.category}"
    }
    if (position % 7 == 0) {
        params.height = 173.toDp(view.context)
        this.constraintTextSeparator.isVisible = true
        this.cardCategory.isVisible = false
        this.textSeparator.text = "Больше новостей"
    }
    Glide.with(view.context)
        .load(item.enclosure.url)
        .into(this.ivMaterial)
    this.tvMaterialTitle.text = item.title
    this.tvCategoryInCard.text = item.category
    this.tvMaterialPublishedDate.text = formatter.format(item.published)
}