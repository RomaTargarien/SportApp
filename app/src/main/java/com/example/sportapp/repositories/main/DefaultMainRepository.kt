package com.example.sportapp.repositories.main

import android.net.Uri
import android.util.Log
import com.example.sportapp.apis.MaterialsApi
import com.example.sportapp.db.NewsDao
import com.example.sportapp.models.User
import com.example.sportapp.models.rss.materials.Item
import com.example.sportapp.models.rss.materials.Rss
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject
import kotlin.concurrent.timerTask

class DefaultMainRepository @Inject constructor(
    private val dao: NewsDao,
    private val materialsApi: MaterialsApi,
    private val users: CollectionReference,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage): MainApiRepository {

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
            val userQuery = users.whereEqualTo("uid",auth.uid).get().addOnCompleteListener {
                val result = it.result
                result?.let {
                    if (!it.isEmpty) {
                        users.document(auth.uid!!).update("likedCategories",likedCatgories).addOnCompleteListener {
                            emiter.onSuccess(Unit)
                        }.addOnFailureListener {
                            emiter.onError(it)
                        }
                    }
                }
            }
        }
    }

    override fun subscribeToRealtimeUpdates(): Observable<User> {
        return Observable.create<User> { emiter ->
            users.document(auth.uid!!).addSnapshotListener { value, error ->
                error?.let {
                    emiter.onError(it)
                }
                value?.let {
                    val categories: List<String>?
                    val name: String
                    val uid: String
                    val userProfileImageUri: String
                    categories = it.get("likedCategories") as List<String>?
                    name = it.get("username") as String
                    uid = it.get("uid") as String
                    userProfileImageUri = it.get("userProfileImageUri") as String
                    if (categories != null) {
                        val user = User(uid,name,categories,userProfileImageUri)
                        Log.d("TAG",user.toString())
                        emiter.onNext(user)
                    } else {
                        val user = User(uid,name, emptyList())
                        emiter.onNext(user)
                    }
                }
            }
        }
    }

    override fun sendVerificationEmail(): Observable<Unit> {
        return Observable.create { emiter ->
            auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    emiter.onNext(Unit)
                } else {
                    emiter.onError(Exception(""))
                }
            }
        }
    }

    override fun updateEmail(email:String): Observable<Unit> {
        return Observable.create { emiter ->
            auth.currentUser?.updateEmail(email)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    emiter.onNext(Unit)
                } else {
                    emiter.onError(Exception(""))
                }
            }
        }
    }

    override fun reauthenticate(password: String): Observable<Unit> {
        return Observable.create { emiter ->
            auth.currentUser?.email?.let {
                val credentials = EmailAuthProvider.getCredential(it,password)
                auth.currentUser!!.reauthenticate(credentials)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            emiter.onNext(Unit)
                        } else {
                            emiter.onError(Exception(""))
                        }
                    }
            }
        }
    }

    override fun updateUserProfileImage(imageUri: Uri): Observable<Unit> {
        return Observable.create { emiter ->
            storage
                .getReference(auth.uid!!)
                .putFile(imageUri)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                    task.result
                        ?.metadata
                        ?.reference
                        ?.downloadUrl
                        ?.addOnCompleteListener { uriTask->
                            if (uriTask.isSuccessful) {
                            users
                                .document(auth.uid!!)
                                .update("userProfileImageUri",uriTask.result.toString())
                                .addOnCompleteListener { uploadTask ->
                                    uploadTask
                                        .addOnCompleteListener {
                                            emiter.onNext(Unit)
                                    }
                                        .addOnFailureListener {
                                            emiter.onError(it)
                                        }

                                }
                                .addOnFailureListener {
                                    emiter.onError(it)
                                }
                        }
                    }
                }
            }
                .addOnFailureListener {
                    emiter.onError(it)
                }
        }
    }

    override fun updatePassword(password: String): Observable<Unit> {
        return Observable.create { emiter ->
            auth.currentUser?.updatePassword(password)
                ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    emiter.onNext(Unit)
                }
                else {
                    emiter.onError(Exception(""))
                }
            }
                ?.addOnFailureListener {
                    emiter.onError(it)
                }
        }
    }

    override fun reloadUserInfo(): Observable<Unit> {
        return Observable.create { emiter ->
            auth.currentUser?.reload()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    emiter.onNext(Unit)
                }
            }
                ?.addOnFailureListener {
                    emiter.onError(it)
                }
        }
    }



    override fun changePassword(): Observable<Unit> {
        return Observable.create { emiter ->
            auth.sendPasswordResetEmail(auth.currentUser!!.email!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        emiter.onNext(Unit)
                    } else {
                        emiter.onError(task.exception)
                    }
                }
        }
    }
}