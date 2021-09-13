package com.example.sportapp.models

data class User(
    val uid: String,
    val username: String,
    val likedCategories: List<String> = emptyList()
    )
