package com.example.sportapp.other.ext

fun String.convertToRssQuery(): String {
    val rssQuery = when (this) {
        "Футбол" -> "football.rss"
        "Теннис" -> "tennis.rss"
        "Хоккей" -> "hockey.rss"
        "Баскетбол" -> "basketball.rss"
        "Формула-1" -> "formula1.rss"
        "Автоспорт" -> "autosport.rss"
        "Бокс" -> "boxing.rss"
        "Зимние виды спорта" -> "winter.rss"
        "Волейбол" -> "voleybol.rss"
        "Гандбол" -> "gandbol.rss"
        "Биатлон" -> "biatlon.rss"
        "Другие виды спорта" -> "other.rss"
         else -> "news.rss"
    }
    return rssQuery
}

fun String.convertRssQueryToCategory(): String {
    val category = when(this) {
        "football.rss" -> "Футбол"
        "tennis.rss" -> "Теннис"
        "hockey.rss" -> "Хоккей"
        "basketball.rss" -> "Баскетбол"
        "formula1.rss" -> "Формула-1"
        "autosport.rss" -> "Автоспорт"
        "boxing.rss" -> "Бокс"
        "winter.rss" -> "Лыжные гонки"
        "voleybol.rss" -> "Волейбол"
        "gandbol.rss" -> "Гандбол"
        "biatlon.rss" -> "Биатлон"
        "other.rss" -> "Другие"
        else -> "%"
    }
    return category
}