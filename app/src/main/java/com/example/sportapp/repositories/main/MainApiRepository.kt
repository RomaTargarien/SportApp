package com.example.sportapp.repositories.main

import androidx.lifecycle.LiveData
import com.example.sportapp.models.rss.materials.Item
import com.example.sportapp.models.rss.materials.Rss
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject

interface MainApiRepository {
    fun getApiMaterials(): Single<Rss>
    fun insertDataInDatabase(items: List<Item>)
    fun fetchDataFromDatabase(): Observable<List<Item>>
    fun fetchWithOffset(offset: Int,limit: Int = 10): List<Item>
    fun fetchWithOffsetObservable(offset: Int,limit: Int): Observable<List<Item>>
    fun delete()
}