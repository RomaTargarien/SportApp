package com.example.sportapp.models.ui

import com.example.sportapp.R

data class Category(val tittle: String, val id: Int,val isLiked: Boolean = false)

val categories = listOf<Category>(
    Category("Футбол", R.drawable.football),
    Category("Теннис",R.drawable.ball),
    Category("Хоккей",R.drawable.hockey_stick),
    Category("Баскетбол",R.drawable.basketball_ball),
    Category("Формула-1",R.drawable.racing_car),
    Category("Автоспорт",R.drawable.convertible_car),
    Category("Бокс",R.drawable.box),
    Category("Зимние виды спорта",R.drawable.skiing),
    Category("Гандбол",R.drawable.biathlon),
    Category("Волейбол",R.drawable.volleyball),
    Category("Другие виды спорта",R.drawable.gymnastic_rings)
)