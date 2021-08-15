package com.example.sportapp.ui.introduction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.sportapp.ui.auth.AuthActivity


interface IIntroRouter {
    fun enterActivity()
}

class IntroRouter(
    val startActivity: AppCompatActivity,
    val activityToOpen: AppCompatActivity
) : IIntroRouter {

    override fun enterActivity() {
        val intent = Intent(startActivity,activityToOpen::class.java)
        startActivity.startActivity(intent)
        startActivity.finish()
    }

}