package com.example.sportapp.repositories.auth

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import io.reactivex.rxjava3.core.Single

interface AuthRepository {

    fun loginWithGoogle(credential: AuthCredential) : Single<AuthResult>

    fun loginRx(email: String,password: String): Single<AuthResult>

    fun restPasswordRx(email: String): Single<Unit>

    fun registerRx(email: String,username: String,password: String) : Single<AuthResult>
}