package com.example.sportapp.ui.auth.viewModels

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.other.Resource
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

class LoginViewModel @ViewModelInject constructor(
    private val repository: AuthRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
): ViewModel() {

    constructor() : this(repository, dispatcher) {
        loginEmail.
                   .subscribeOn(AndroidSchedulers.mainThread())
           .debounce(1000,TimeUnit.MILLISECONDS)
           .observeOn(Schedulers.computation())
           .map {  checkEmail(it) }
           .observeOn(AndroidSchedulers.mainThread())
            .subscribe(isSnackbarShown)
//           .subscribe({
//               isSnackbarShown.onNext(it)
  //             binding.textInputLoginEmail.enableError(it)
//                      }, { },{ })
    }

    private val _loginStatus = MutableLiveData<Resource<AuthResult>>()
    val loginStatus: LiveData<Resource<AuthResult>> = _loginStatus

    val loginPassword = BehaviorSubject.create<String>()

    val loginAction = PublishSubject.create<Boolean>()

    val isSnackbarShown = PublishSubject.create<Boolean>()

    val loginEmail = BehaviorSubject.create<String>()

    fun loginRX(email: String,password: String) {
        val x = repository.loginRx(email,password)
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

    fun loginWithGoogle(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken,null)
        viewModelScope.launch(dispatcher) {
            val result = repository.loginWithGoogle(credentials)
            _loginStatus.postValue(result)
        }
    }
}