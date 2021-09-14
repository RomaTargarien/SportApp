package com.example.sportapp.other.ext

import com.example.sportapp.R

fun String.returnIconId() =
    when (this) {
        "Футбол" -> R.drawable.football
        "Теннис" -> R.drawable.ball
        "Хоккей" -> R.drawable.hockey_stick
        "Баскетбол" -> R.drawable.basketball_ball
        "Формула-1" -> R.drawable.racing_car
        "Автоспорт" -> R.drawable.convertible_car
        "Бокс" -> R.drawable.box
        "Волейбол" -> R.drawable.volleyball
        "Гандбол" -> R.drawable.handball
        "Биатлон" -> R.drawable.biathlon
        "Другие виды спорта" -> R.drawable.gymnastic_rings
        else -> R.drawable.gymnastic_rings
    }