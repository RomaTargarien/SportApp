package com.example.sportapp.repositories

import com.example.sportapp.other.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface AuthRepository {

    suspend fun register(email: String,username: String,password: String) : Resource<AuthResult>

    suspend fun login(email: String,password: String) : Resource<AuthResult>

    suspend fun loginWithGoogle(credential: AuthCredential) : Resource<AuthResult>

    suspend fun resetPassword(email: String)

    fun loginRx(email: String,password: String): Single<AuthResult>

    fun restPasswordRx(email: String): Single<Void>

    fun registerRx(email: String,username: String,password: String) : Single<AuthResult>
}