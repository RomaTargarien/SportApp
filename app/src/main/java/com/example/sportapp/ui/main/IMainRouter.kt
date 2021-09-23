package com.example.sportapp.ui.main

import android.os.Bundle

interface IMainRouter {
    fun fromHomeScreenToSelectedItemScreen(bundle: Bundle)
    fun fromHomeSreenToSelectedCategoryScreen(bundle: Bundle)
    fun fromCategoryScreenToSelectedCategoryScreen(bundle: Bundle)
    fun fromSelectedCategoryScreenToItemScreen(bundle: Bundle)
    fun fromUserScreenToChangePasswordScreen()
    fun fromChangePasswordScreenToUserScreen()
    fun fromChangeEmailScreenToUserScreen()
    fun goToUserScreen()
    fun fromUserScreenToChangeEmailScreen()
    fun logOut()
}