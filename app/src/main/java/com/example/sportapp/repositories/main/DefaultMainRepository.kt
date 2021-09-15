package com.example.sportapp.repositories.main

import android.util.Log
import com.example.sportapp.apis.MaterialsApi
import com.example.sportapp.db.NewsDao
import com.example.sportapp.models.User
import com.example.sportapp.models.rss.materials.Item
import com.example.sportapp.models.rss.materials.Rss
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject

class DefaultMainRepository @Inject constructor(private val dao: NewsDao,private val materialsApi: MaterialsApi): MainApiRepository {

    val users = FirebaseFirestore.getInstance().collection("users")
    val uid = FirebaseAuth.getInstance().uid

    override fun getApiMaterials(rssQuery: String): Single<Rss> {
        return Single.create { emiter ->
            val call = materialsApi.getMaterials(rssQuery)
            if (call.isCanceled) {
                emiter.onError(Exception(""))
            } else {
                val response = call.execute()
                if (response.isSuccessful) {
                    emiter.onSuccess(response.body())
                } else {
                    emiter.onError(Exception(""))
                }
            }
        }
    }

    override fun insertDataInDatabase(items: List<Item>) {
        dao.putNewItems(items)
    }

    override fun fetchDataFromDatabase(): List<Item> {
        return dao.getAllNews()
    }

    override fun fetchWithOffset(offset: Int,limit: Int,category: String): List<Item> {
        Log.d("TAG","fetch $category")
        return dao.getItemsWithOffset(offset,limit,category)
    }

    override fun delete() {
        dao.delete()
    }

    override fun updateUsersLikedCategories(likedCatgories: List<String>): Single<Unit> {
        return Single.create { emiter ->
            val userQuery = users.whereEqualTo("uid",uid).get().addOnCompleteListener {
                val result = it.result
                result?.let {
                    if (!it.isEmpty) {
                        users.document(uid!!).update("likedCategories",likedCatgories).addOnCompleteListener {
                            emiter.onSuccess(Unit)
                        }.addOnFailureListener {
                            emiter.onError(it)
                        }
                    }
                }
            }
        }
    }

    override fun subscribeToRealtimeUpdates(): Observable<List<String>> {
        return Observable.create<List<String>> { emiter ->
            users.document(uid!!).addSnapshotListener { value, error ->
                error?.let {
                    emiter.onError(it)
                }
                value?.let {
                    val categories: List<String>?
                    categories = it.get("likedCategories") as List<String>?
                    if (categories != null) {
                        emiter.onNext(categories)
                    } else {
                        emiter.onNext(emptyList())
                    }
                }
            }
        }
    }
}