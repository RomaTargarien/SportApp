package com.example.sportapp.ui.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.transition.TransitionManager
import com.example.sportapp.R
import com.example.sportapp.databinding.ActivityMainBinding
import com.example.sportapp.other.toDp
import com.example.sportapp.ui.auth.AuthActivity
import com.example.sportapp.ui.main.dialogs.dialogViewModels.EmailVerifyingViewModel
import com.example.sportapp.ui.main.dialogs.dialogViewModels.PasswordChangeDialogViewModel
import com.example.sportapp.ui.main.viewModels.*
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var selectedCategoryViewModel: SelectedCategoryViewModel
    private lateinit var emailChangeViewModel: EmailChangeViewModel
    private lateinit var homeViewModel: HomeFragmentViewModel
    private lateinit var categoriesViewModel: CategoriesViewModel
    private lateinit var userViewModel: UserFragmentViewModel
    private lateinit var passwordChangeDialogViewModel: PasswordChangeDialogViewModel
    private lateinit var emailVerifyingViewModel: EmailVerifyingViewModel
    private lateinit var router: IMainRouter


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        navigationBarColor()
        noInternetChecking()

        //ViewModelInitializing
        selectedCategoryViewModel = ViewModelProvider(this).get(SelectedCategoryViewModel::class.java)
        homeViewModel = ViewModelProvider(this).get(HomeFragmentViewModel::class.java)
        categoriesViewModel = ViewModelProvider(this).get(CategoriesViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserFragmentViewModel::class.java)
        passwordChangeDialogViewModel = ViewModelProvider(this).get(PasswordChangeDialogViewModel::class.java)
        emailVerifyingViewModel = ViewModelProvider(this).get(EmailVerifyingViewModel::class.java)
        emailChangeViewModel = ViewModelProvider(this).get(EmailChangeViewModel::class.java)

        router = MainRouter(this,binding,homeViewModel)

        //navigation
        homeViewModel.goToSelectedCategoryScreen.subscribe({
            router.fromHomeSreenToSelectedCategoryScreen(it)
        },{})
        homeViewModel.goToSelectedItemScreen.subscribe({
            router.fromHomeScreenToSelectedItemScreen(it)
        },{})
        categoriesViewModel.goToSelectedCategoryScreen.subscribe({
            router.fromCategoryScreenToSelectedCategoryScreen(it)
        },{})
        selectedCategoryViewModel.goToSelectedItemScreen.subscribe({
            router.fromSelectedCategoryScreenToItemScreen(it)
        },{})
        emailVerifyingViewModel.verifyingWasSent.subscribe({
            router.logOut()
        },{})

        passwordChangeDialogViewModel.passwordHasChanged.subscribe({
            router.logOut()
        },{})
        emailChangeViewModel.goToLoginScreen.subscribe({
            router.logOut()
        },{})
        userViewModel.logOut.subscribe({
            router.logOut()
        },{})
        binding.tvLogOut.setOnClickListener {
            router.logOut()
        }
        userViewModel.goToChangeEmailScreen.subscribe({
            router.fromUserScreenToChangeEmailScreen()
        },{})
        binding.ibPerson.setOnClickListener {
            router.goToUserScreen()
        }

        //Hiding views
        userViewModel.isLogOutShown.subscribe({
            TransitionManager.beginDelayedTransition(binding.container)
            binding.tvLogOut.isVisible = it
        },{})

        selectedCategoryViewModel.isBottomNavMenuHiden.subscribe({
            if (it) {
                binding.flFragmentContainer.setPadding(0,40.toDp(this),0,0)
            } else {
                binding.flFragmentContainer.setPadding(0,40.toDp(this),0,56.toDp(this))
            }
            binding.bottomAppBar.isVisible = !it
        },{})
    }

    private fun navigationBarColor() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = resources.getColor(R.color.black)
        }
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