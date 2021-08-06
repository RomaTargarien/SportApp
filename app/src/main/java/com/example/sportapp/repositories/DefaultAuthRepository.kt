package com.example.sportapp.repositories

import android.util.Log
import com.example.sportapp.data.User
import com.example.sportapp.other.Resource
import com.example.sportapp.other.safeCall
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.concurrent.Executors

class DefaultAuthRepository : AuthRepository {

    val auth = FirebaseAuth.getInstance()
    val users = FirebaseFirestore.getInstance().collection("users")
    val executor = Executors.newSingleThreadExecutor()

    override fun registerRx(email: String, username: String, password: String): Single<AuthResult> {
        return Single.create { emiter ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = task.result?.user?.uid!!
                        val user = User(uid,username)
                        users.document(uid).set(user)
                        emiter.onSuccess(task.result)
                    } else {
                        emiter.onError(task.exception)
                    }
            }
        }
    }

    override fun loginWithGoogle(credential: AuthCredential): Single<AuthResult> {
        return Single.create { emiter ->
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        emiter.onSuccess(task.result)
                    } else {
                        emiter.onError(task.exception)
                    }
                }
        }
    }

    override fun restPasswordRx(email: String): Single<Void> {
        return Single.create { emiter ->
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(executor) { task ->
                    if (task.isComplete) {
                        emiter.onSuccess(task.result)
                    } else {
                        emiter.onError(task.exception)
                    }
                }
        }
    }

    override fun loginRx(email: String, password: String): Single<AuthResult> {
        return Single.create { emiter ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(executor) { task ->
                    if (task.isSuccessful) {
                        Log.d("TAG",task.isSuccessful.toString())
                        emiter.onSuccess(task.result)
                    } else {
                        Log.d("TAG",task.isCanceled.toString())
                        emiter.onError(task.exception)
                    }
                }
        }
    }
}












