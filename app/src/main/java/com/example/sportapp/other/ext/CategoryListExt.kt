package com.example.sportapp.other.ext

import com.example.sportapp.models.ui.Category

fun List<Category>.getListOfTittles(): List<String> {
    val list = mutableListOf<String>()
    for (category in this) {
        list.add(category.tittle)
    }
    return list
}

fun List<Category>.getIndexOfCategory(tittle: String): Int {
    for (category in this) {
        if (tittle.equals(category.tittle)) {
            return this.indexOf(category)
        }
    }
    return 0
}