package com.example.sportapp.ui.auth.viewModels

import android.content.Context
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.other.Resource
import com.example.sportapp.other.validateEmail
import com.example.sportapp.other.validatePassword
import com.example.sportapp.repositories.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class LoginViewModel @ViewModelInject constructor(
    private val repository: AuthRepository,
    private val applicationContext: Context
): ViewModel() {

    val _loginStatus = MutableLiveData<Resource<AuthResult>>()
    val loginStatus: LiveData<Resource<AuthResult>> = _loginStatus

    val _loginPassword = BehaviorSubject.create<String>()
    val loginPassword = BehaviorSubject.create<Resource<String>>()

    val loginButtonEnabled = BehaviorSubject.createDefault(false)

    val isSnackbarShown = PublishSubject.create<Boolean>()

    val _loginEmail = BehaviorSubject.create<String>()
    val loginEmail = BehaviorSubject.create<Resource<String>>()

    val logIn = BehaviorSubject.createDefault(false)

    init {
       val emailSubject = _loginEmail
            .subscribeOn(AndroidSchedulers.mainThread())
            .debounce(1000,TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .observeOn(Schedulers.computation())
            .map { it.validateEmail(applicationContext) }
            .observeOn(AndroidSchedulers.mainThread())
            .share()

        emailSubject.subscribe(loginEmail)

        val passwordSubject = _loginPassword
            .subscribeOn(AndroidSchedulers.mainThread())
            .debounce(1000,TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .observeOn(Schedulers.computation())
            .map { it.validatePassword(applicationContext) }
            .observeOn(AndroidSchedulers.mainThread())
            .share()

        passwordSubject.subscribe(loginPassword)

        Observable.combineLatest(emailSubject,passwordSubject, { first,second ->
            first is Resource.Success && second is Resource.Success
        }).subscribe({
            loginButtonEnabled.onNext(it)
        },{},{})


        logIn.subscribe({
            if (it) {
                repository.loginRx(_loginEmail.value,_loginPassword.value)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe {
                        _loginStatus.postValue(Resource.Loading())
                    }
                    .doOnDispose {
                        _loginStatus.postValue(Resource.Error("Canceled"))
                    }
                    .subscribe({
                        _loginStatus.postValue(Resource.Success(it))
                    }, {
                        _loginStatus.postValue(Resource.Error(it.message ?: ""))
                    })
            }
        },{})
    }

    fun loginWithGoogle(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken,null)
        repository.loginWithGoogle(credentials)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _loginStatus.postValue(Resource.Success(it))
            },{
                _loginStatus.postValue(Resource.Error("Log in failed"))
            })
    }
}