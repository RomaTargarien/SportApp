package com.example.sportapp.apis

import com.example.sportapp.data.rss.materials.Rss
import io.reactivex.rxjava3.core.Observable
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import java.util.*

interface MaterialsApi {

    @GET("rss/main.xml")
    fun getMaterials() : Call<Rss>
}