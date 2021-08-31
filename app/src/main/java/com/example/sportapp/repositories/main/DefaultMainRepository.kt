package com.example.sportapp.repositories.main

import android.util.Log
import com.example.sportapp.apis.MaterialsApi
import com.example.sportapp.data.rss.materials.Rss
import com.example.sportapp.db.NewsDao
import io.reactivex.rxjava3.core.Single
import java.lang.Exception

class DefaultMainRepository(
    private var materialsApi: MaterialsApi
    ) : MainRepository {


    override fun getMaterials(): Single<Rss> {
        return Single.create { emiter ->
            Log.d("TAG","1")
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
}