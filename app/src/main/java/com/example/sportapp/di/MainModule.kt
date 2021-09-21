package com.example.sportapp.di

import android.content.Context
import androidx.room.Room
import com.example.sportapp.apis.MaterialsApi
import com.example.sportapp.db.NewsDao
import com.example.sportapp.db.NewsDatabase
import com.example.sportapp.other.Constants.BASE_URL
import com.example.sportapp.repositories.main.DefaultMainRepository
import com.example.sportapp.repositories.main.MainApiRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object MainModule {

    @Provides
    @ActivityScoped
    fun provideMaterialsApi(): MaterialsApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(TikXmlConverterFactory.create())
        .build()
        .create(MaterialsApi::class.java)

    @Provides
    @ActivityScoped
    fun provideMainApiRepository(
        materialsApi: MaterialsApi,
        dao: NewsDao,
        collectionReference: CollectionReference,
        auth: FirebaseAuth,
        storage: FirebaseStorage
    ) = DefaultMainRepository(dao,materialsApi,collectionReference,auth,storage) as MainApiRepository


    @Provides
    @ActivityScoped
    fun provideNewsDatabase(@ApplicationContext app: Context) = Room.databaseBuilder(
        app,NewsDatabase::class.java,"news_db"
    ).fallbackToDestructiveMigration().build()


    @Provides
    @ActivityScoped
    fun provideNewsDao(db: NewsDatabase) = db.getNewsDao()

    @Provides
    @ActivityScoped
    fun provideFirestoreCollection() = FirebaseFirestore.getInstance().collection("users")

    @Provides
    @ActivityScoped
    fun provideAuthInstance() = FirebaseAuth.getInstance()

    @Provides
    @ActivityScoped
    fun provideStorage() = Firebase.storage
}