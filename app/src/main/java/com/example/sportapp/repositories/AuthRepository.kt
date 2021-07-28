package com.example.sportapp.repositories

import com.example.sportapp.other.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult

interface AuthRepository {

    suspend fun register(email: String,username: String,password: String) : Resource<AuthResult>

    suspend fun login(email: String,password: String) : Resource<AuthResult>

    suspend fun loginWithGoogle(credential: AuthCredential) : Resource<AuthResult>

    suspend fun resetPassword(email: String)
}