package com.example.sportapp.ui.main

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.sportapp.R
import com.example.sportapp.databinding.ActivityMainBinding
import com.example.sportapp.other.toDp
import com.example.sportapp.ui.auth.AuthActivity
import com.example.sportapp.ui.main.viewModels.CategoriesViewModel
import com.example.sportapp.ui.main.viewModels.HomeFragmentViewModel
import com.example.sportapp.ui.main.viewModels.SelectedCategoryViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var selectedCategoryViewModel: SelectedCategoryViewModel
    private lateinit var homeViewModel: HomeFragmentViewModel
    private lateinit var categoriesViewModel: CategoriesViewModel
    private lateinit var router: IMainRouter


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //ViewModelInitializing
        selectedCategoryViewModel = ViewModelProvider(this).get(SelectedCategoryViewModel::class.java)
        homeViewModel = ViewModelProvider(this).get(HomeFragmentViewModel::class.java)
        categoriesViewModel = ViewModelProvider(this).get(CategoriesViewModel::class.java)
        router = MainRouter(this,binding)

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = resources.getColor(R.color.black)
        }
        noInternetChecking()

        //navigation
        homeViewModel.goToSelectedCategoryScreen.subscribe({
            router.fromHomeSreenToSelectedCategoryScreen(it)
        },{})
        homeViewModel.goToSelectedItemScreen.subscribe({
            router.fromHomeScreenToSelectedItemScreen(it)
        },{})
        categoriesViewModel.goToSelectedCategoryScreen.subscribe({
            Log.d("TAG",it.toString())
            router.fromCategoryScreenToSelectedCategoryScreen(it)
        },{})
        selectedCategoryViewModel.goToSelectedItemScreen.subscribe({
            router.fromSelectedCategoryScreenToItemScreen(it)
        },{})
        selectedCategoryViewModel.isBottomNavMenuHiden.subscribe({
            if (it) {
                binding.flFragmentContainer.setPadding(0,0,0,0)
            } else {
                binding.flFragmentContainer.setPadding(0,0,0,56.toDp(this))
            }
            binding.bottomAppBar.isVisible = !it
        },{})


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.iLogOut -> {
                FirebaseAuth.getInstance().signOut()
                Intent(this, AuthActivity::class.java).also {
                    startActivity(it)
                }
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun noInternetChecking() {
        NoInternetDialogSignal.Builder(
            this,
            lifecycle
        ).apply {
            dialogProperties.apply {
                connectionCallback = object : ConnectionCallback { // Optional
                    override fun hasActiveConnection(hasActiveConnection: Boolean) {
                        Log.d("TAG","connection ${hasActiveConnection.toString()}")
                    }
                }

                cancelable = false // Optional
                noInternetConnectionTitle = "No Internet" // Optional
                noInternetConnectionMessage =
                    "Check your Internet connection and try again." // Optional
                showInternetOnButtons = true // Optional
                pleaseTurnOnText = "Please turn on" // Optional
                wifiOnButtonText = "Wifi" // Optional
                mobileDataOnButtonText = "Mobile data" // Optional

                onAirplaneModeTitle = "No Internet" // Optional
                onAirplaneModeMessage = "You have turned on the airplane mode." // Optional
                pleaseTurnOffText = "Please turn off" // Optional
                airplaneModeOffButtonText = "Airplane mode" // Optional
                showAirplaneModeOffButtons = true // Optional
            }
        }.build()
    }
}