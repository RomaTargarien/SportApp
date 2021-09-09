package com.example.sportapp.repositories.main

import com.example.sportapp.models.rss.materials.Item
import com.example.sportapp.models.rss.materials.Rss
import com.example.sportapp.other.Constants.LIMIT
import com.example.sportapp.other.Constants.OFFSET
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject

interface MainApiRepository {
    fun getApiMaterials(): Single<Rss>
    fun insertDataInDatabase(items: List<Item>)
    fun fetchDataFromDatabase(): Observable<List<Item>>
    fun delete()
    fun fetchWithOffset(offset: Int,limit: Int = LIMIT): List<Item>
}