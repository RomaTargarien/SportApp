package com.example.sportapp.ui.introduction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.sportapp.R
import com.example.sportapp.adapters.ViewPagerIntroAdapter
import com.example.sportapp.data.ui.Intro
import com.example.sportapp.databinding.ActivityIntroBinding
import com.example.sportapp.ui.auth.AuthActivity

class IntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroBinding
    val dots = mutableListOf<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val introItems = listOf(
            Intro(R.raw.cup,"Welcome to Sporter", "Invastigate new horizons of sport"),
            Intro(R.raw.tennis,"Everything you need right here", "All sport news in one application, stay in touch and up to date"),
            Intro(R.raw.target,"Welcome to Sporter", "Invastigate new horizons of sport"),
        )
        dotsIndicator(introItems.size-1)
        binding.vpIntro.apply {
            adapter = ViewPagerIntroAdapter(introItems)
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }
        binding.vpIntro.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                selectedIndicator(position)
                super.onPageSelected(position)
            }
        })
        binding.bnGoToAuth.setOnClickListener {
            Intent(this, AuthActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }
    private fun selectedIndicator(position: Int) {
        for (i in 0..dots.size-1) {
            if (i == position) {
                dots[i].setTextColor(resources.getColor(R.color.purple_500))
            } else {
                dots[i].setTextColor(resources.getColor(R.color.grey))
            }
        }
    }
    private fun dotsIndicator(size: Int) {
        for (i in 0..size) {
            dots.add(TextView(this))
            dots[i].setText(Html.fromHtml("&#9679"))
            dots[i].textSize = 18f
            binding.dotsContainer.addView(dots[i])
        }
    }
}