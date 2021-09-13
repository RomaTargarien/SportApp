package com.example.sportapp.apis

import com.example.sportapp.models.rss.materials.Rss
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path

interface MaterialsApi {

    @GET("/rssfeeds/{rssQuery}")
    fun getMaterials(@Path("rssQuery") rssQuery: String = "news.rss") : Call<Rss>
}