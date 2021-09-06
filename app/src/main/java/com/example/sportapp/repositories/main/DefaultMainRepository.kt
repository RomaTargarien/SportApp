package com.example.sportapp.repositories.main

import android.util.Log
import com.example.sportapp.apis.MaterialsApi
import com.example.sportapp.db.NewsDao
import com.example.sportapp.models.rss.materials.Item
import com.example.sportapp.models.rss.materials.Rss
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.Exception
import javax.inject.Inject

class DefaultMainRepository @Inject constructor(): MainApiRepository {

    @Inject
    lateinit var materialsApi: MaterialsApi

    @Inject
    lateinit var dao: NewsDao


    override fun getApiMaterials(): Single<Rss> {
        return Single.create { emiter ->
            val call = materialsApi.getMaterials()
            if (!call.isExecuted) {
                val response = call.execute()
                if (response.isSuccessful) {
                    emiter.onSuccess(response.body())
                } else {
                    emiter.onError(Exception(""))
                }
            } else if (call.isCanceled) {
                emiter.onError(Exception(""))
            } else {
                emiter.onError(Exception(""))
            }
        }
    }

    override fun insertDataInDatabase(items: List<Item>) {
        dao.putNewItems(items)
    }

    override fun fetchDataFromDatabase(): Observable<List<Item>> {
        return dao.getAllNews()
    }

    override fun fetchWithOffset(offset: Int,limit: Int): List<Item> {
        return dao.getItemsWithOffset(offset,limit)
    }

    override fun delete() {
        dao.delete()
    }


}