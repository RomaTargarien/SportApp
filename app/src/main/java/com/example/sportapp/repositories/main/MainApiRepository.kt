package com.example.sportapp.repositories.main

import android.net.Uri
import com.example.sportapp.models.User
import com.example.sportapp.models.rss.materials.Item
import com.example.sportapp.models.rss.materials.Rss
import com.example.sportapp.other.Constants.LIMIT
import com.example.sportapp.other.Constants.OFFSET
import com.google.firebase.auth.UserProfileChangeRequest
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject

interface MainApiRepository {
    fun getApiMaterials(rssQuery: String = "news.rss"): Single<Rss>
    fun insertDataInDatabase(items: List<Item>)
    fun fetchDataFromDatabase(): List<Item>
    fun delete()
    fun fetchWithOffset(offset: Int,limit: Int = LIMIT,category: String = "%"): List<Item>
    fun updateUsersLikedCategories(likedCatgories: List<String>): Single<Unit>
    fun subscribeToRealtimeUpdates(): Observable<User>
    fun sendVerificationEmail(): Observable<Unit>
    fun updateEmail(email:String): Observable<Unit>
    fun changePassword(): Observable<Unit>
    fun reauthenticate(password: String): Observable<Unit>
    fun updateUserProfileImage(imageUri: Uri): Observable<Unit>
    fun updatePassword(password: String): Observable<Unit>
    fun reloadUserInfo(): Observable<Unit>
}