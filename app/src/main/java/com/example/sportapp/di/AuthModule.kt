package com.example.sportapp.di

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.sportapp.repositories.AuthRepository
import com.example.sportapp.repositories.DefaultAuthRepository
import com.example.sportapp.ui.auth.AuthActivity
import com.example.sportapp.ui.auth.fragments.LoginFragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Singleton


@Module
@InstallIn (ActivityComponent::class)
object AuthModule {

    @Provides
    @ActivityScoped
    fun provideAuthRepository() = DefaultAuthRepository() as AuthRepository

}










