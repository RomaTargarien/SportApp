package com.example.sportapp.di

import com.example.sportapp.repositories.auth.AuthRepository
import com.example.sportapp.repositories.auth.DefaultAuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped


@Module
@InstallIn (ActivityComponent::class)
object AuthModule {

    @Provides
    @ActivityScoped
    fun provideAuthRepository() = DefaultAuthRepository() as AuthRepository

}










