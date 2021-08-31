package com.example.sportapp.repositories.main

import com.example.sportapp.data.rss.materials.Rss
import io.reactivex.rxjava3.core.Single
import retrofit2.Call

interface MainRepository {
    fun getMaterials(): Single<Rss>
}