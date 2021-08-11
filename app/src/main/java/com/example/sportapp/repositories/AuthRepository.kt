package com.example.sportapp.repositories

import com.example.sportapp.other.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface AuthRepository {

    fun loginWithGoogle(credential: AuthCredential) : Single<AuthResult>

    fun loginRx(email: String,password: String): Single<AuthResult>

    fun restPasswordRx(email: String): Completable

    fun registerRx(email: String,username: String,password: String) : Single<AuthResult>
}