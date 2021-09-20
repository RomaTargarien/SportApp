package com.example.sportapp.models

import com.example.sportapp.other.Constants.DEFAULT_PROFILE_PICTURE_URL

data class User(
    val uid: String,
    val username: String,
    val likedCategories: List<String> = emptyList(),
    val userProfileImageUri: String = DEFAULT_PROFILE_PICTURE_URL
    )
