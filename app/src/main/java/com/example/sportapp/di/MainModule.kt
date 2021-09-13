package com.example.sportapp.di

import android.content.Context
import androidx.room.Room
import com.example.sportapp.apis.MaterialsApi
import com.example.sportapp.db.NewsDatabase
import com.example.sportapp.other.Constants.BASE_URL
import com.example.sportapp.repositories.main.DefaultMainApiRepository
import com.example.sportapp.repositories.main.MainApiRepository
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object MainModule {

    @Singleton
    @Provides
    fun provideMaterialsApi(): MaterialsApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(TikXmlConverterFactory.create())
        .build()
        .create(MaterialsApi::class.java)

    @Singleton
    @Provides
    fun provideMainApiRepository() =
        DefaultMainApiRepository(provideMaterialsApi()) as MainApiRepository

    @Singleton
    @Provides
    fun provideNewsDatabase(@ApplicationContext app: Context) = Room.databaseBuilder(
        app,NewsDatabase::class.java,"news_db"
    ).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideNewsDao(db: NewsDatabase) = db.getNewsDao()

}