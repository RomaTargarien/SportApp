package com.example.sportapp.apis

import com.example.sportapp.models.rss.materials.Rss
import retrofit2.Call
import retrofit2.http.GET

interface MaterialsApi {

    @GET("rss/main.xml")
    fun getMaterials() : Call<Rss>
}