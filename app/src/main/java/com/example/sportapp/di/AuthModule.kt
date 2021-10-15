package com.example.sportapp.di

import android.content.Context
import com.example.sportapp.other.ValidationImpl
import com.example.sportapp.other.ValidationImpl.Companion.EmailValidation
import com.example.sportapp.other.ValidationImpl.Companion.PasswordValidation
import com.example.sportapp.other.ValidationImpl.Companion.UsernameValidation
import com.example.sportapp.repositories.auth.AuthRepository
import com.example.sportapp.repositories.auth.DefaultAuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthRepository() = DefaultAuthRepository() as AuthRepository

    @Provides
    @Singleton
    fun provideEmailValidation(@ApplicationContext context: Context) =
        ValidationImpl.EmailValidationImpl(context) as EmailValidation

    @Provides
    @Singleton
    fun providePasswordValidation(@ApplicationContext context: Context) =
        ValidationImpl.PasswordValidationImpl(context) as PasswordValidation

    @Provides
    @Singleton
    fun provideUsernameValidation(@ApplicationContext context: Context) =
        ValidationImpl.UsernameValidationImpl(context) as UsernameValidation

}










