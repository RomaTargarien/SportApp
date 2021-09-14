package com.example.sportapp.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.sportapp.R
import com.example.sportapp.databinding.ActivityMainBinding

class MainRouter(val mainActivity: AppCompatActivity,val binding: ActivityMainBinding) : IMainRouter {

    init {

        val navHostFragment = mainActivity.supportFragmentManager.findFragmentById(R.id.navHostFragmentMain) as NavHostFragment

        binding.bottomNavigationView.apply {
            background = null
            setupWithNavController(navHostFragment.findNavController())
        }
    }

    override fun fromHomeScreenToSelectedItemScreen(bundle: Bundle) {
        mainActivity.findNavController(R.id.navHostFragmentMain).navigate(R.id.action_homeFragment_to_itemFragment,bundle)
    }

    override fun fromHomeSreenToSelectedCategoryScreen(bundle: Bundle) {
        mainActivity.findNavController(R.id.navHostFragmentMain).navigate(R.id.action_homeFragment_to_selectedCategoryFragment,bundle)
    }

    override fun fromCategoryScreenToSelectedCategoryScreen(bundle: Bundle) {
        mainActivity.findNavController(R.id.navHostFragmentMain).navigate(R.id.action_categoriesFragment_to_selectedCategoryFragment,bundle)
    }

    override fun fromSelectedCategoryScreenToItemScreen(bundle: Bundle) {
        mainActivity.findNavController(R.id.navHostFragmentMain).navigate(R.id.action_selectedCategoryFragment_to_itemFragment,bundle)
    }

}