package com.example.sportapp.screen.main

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.transition.TransitionManager
import com.example.sportapp.R
import com.example.sportapp.databinding.ActivityMainBinding
import com.example.sportapp.other.toDp
import com.example.sportapp.screen.main.categories.CategoriesViewModel
import com.example.sportapp.service.MaterialsService
import com.example.sportapp.screen.main.dialogs.email_verification.EmailVerifyingViewModel
import com.example.sportapp.screen.main.home.HomeFragmentViewModel
import com.example.sportapp.screen.main.profile.UserFragmentViewModel
import com.example.sportapp.screen.main.profile.change_email.EmailChangeViewModel
import com.example.sportapp.screen.main.profile.change_password.PasswordChangeViewModel
import com.example.sportapp.screen.main.selected_category.SelectedCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var router: IMainRouter
    private val homeViewModel: HomeFragmentViewModel by viewModels()
    private val selectedCategoryViewModel: SelectedCategoryViewModel by viewModels()
    private val emailChangeViewModel: EmailChangeViewModel by viewModels()
    private val categoriesViewModel: CategoriesViewModel by viewModels()
    private val userViewModel: UserFragmentViewModel by  viewModels()
    private val passwordChangeViewModel: PasswordChangeViewModel by viewModels()
    private val emailVerifyingViewModel: EmailVerifyingViewModel by viewModels()
    private lateinit var service: MaterialsService
    private lateinit var intentService: Intent


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        navigationBarColor()
        noInternetChecking()
        service = MaterialsService()
        intentService = Intent(this,service::class.java)

        if (isServiceRunning(service::class.java)) {
            stopService(intentService)
        }

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
            //router.logOut()
        },{})

        passwordChangeViewModel.goToUserScreen.subscribe({
            userViewModel.reauthenticationMessage.onNext(it)
            router.fromChangePasswordScreenToUserScreen()

        },{})
        emailChangeViewModel.goToUserScreen.subscribe({
            userViewModel.reauthenticationMessage.onNext(it)
            router.fromChangeEmailScreenToUserScreen()
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
        userViewModel.goToChangePasswordScreen.subscribe({
            router.fromUserScreenToChangePasswordScreen()
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

    override fun onDestroy() {
        super.onDestroy()
        if (!isServiceRunning(service::class.java)) {
            startService(intentService)
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

    @SuppressLint("RestrictedApi")
    override fun onBackPressed() {
        super.onBackPressed()
        Log.d("TAG",findNavController(R.id.navHostFragmentMain).backStack.toString())
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        // Loop through the running services
        for (service in activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                // If the service is running then return true
                return true
            }
        }
        return false
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context,MainActivity::class.java)
        }
    }
}