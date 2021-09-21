package com.example.sportapp.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.sportapp.R
import com.example.sportapp.databinding.ActivityMainBinding
import com.example.sportapp.ui.auth.AuthActivity
import com.example.sportapp.ui.main.viewModels.base.DataProviderViewModel
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("RestrictedApi")
class MainRouter(
    val mainActivity: AppCompatActivity,
    val binding: ActivityMainBinding,
    val viewModel: DataProviderViewModel
    ) : IMainRouter {

    init {

        val navHostFragment = mainActivity.supportFragmentManager.findFragmentById(R.id.navHostFragmentMain) as NavHostFragment

        binding.bottomNavigationView.apply {
            background = null
            setupWithNavController(navHostFragment.findNavController())
            setOnItemReselectedListener {
                when (it.itemId) {
                    R.id.homeFragment -> {
                        viewModel.smoothScrollToFirstPosition.onNext(Unit)
                    }
                }
            }
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

    override fun goToUserScreen() {
        mainActivity.findNavController(R.id.navHostFragmentMain).navigate(R.id.userFragment)
    }

    override fun fromUserScreenToChangeEmailScreen() {
        mainActivity.findNavController(R.id.navHostFragmentMain).navigate(R.id.action_userFragment_to_emailChangeFragment)
    }

    override fun fromUserScreenToChangePasswordScreen() {
        mainActivity.findNavController(R.id.navHostFragmentMain).navigate(R.id.action_userFragment_to_passwordChangeFragmnet)
    }

    override fun fromChangeEmailScreenToUserScreen() {
        mainActivity.findNavController(R.id.navHostFragmentMain).navigate(R.id.action_emailChangeFragment_to_userFragment)
    }

    override fun fromChangePasswordScreenToUserScreen() {
        mainActivity.findNavController(R.id.navHostFragmentMain).navigate(R.id.action_passwordChangeFragmnet_to_userFragment)
    }

    override fun logOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(mainActivity,AuthActivity::class.java)
        mainActivity.startActivity(intent)
        mainActivity.finish()
    }

}