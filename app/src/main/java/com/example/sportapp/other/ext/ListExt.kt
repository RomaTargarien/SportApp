package com.example.sportapp.other.ext

import com.example.sportapp.models.rss.materials.Item

fun List<Item>.itemsToInsert(oldItems: List<Item>): List<Item> {
    val itemsToInsert = mutableListOf<Item>()
    for (item in this) {
        if (!(item in oldItems)) {
            itemsToInsert.add(item)
        } else {
            break
        }
    }
    return itemsToInsert
}