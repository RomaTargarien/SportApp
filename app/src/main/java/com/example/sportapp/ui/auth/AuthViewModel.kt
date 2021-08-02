package com.example.sportapp.ui.auth

import android.content.Context
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.R
import com.example.sportapp.other.Constants.MAX_USERNAME_LENGHT
import com.example.sportapp.other.Constants.MIN_PASSWORD_LENGHT
import com.example.sportapp.other.Constants.MIN_USERNAME_LENGHT
import com.example.sportapp.other.Resource
import com.example.sportapp.repositories.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class AuthViewModel @ViewModelInject constructor(
    private val repository: AuthRepository,
    private val applicationContext: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
): ViewModel() {

    private val _registerStatus = MutableLiveData<Resource<AuthResult>>()
    val registerStatus: LiveData<Resource<AuthResult>> = _registerStatus

    private val _loginStatus = MutableLiveData<Resource<AuthResult>>()
    val loginStatus: LiveData<Resource<AuthResult>> = _loginStatus

    private val _passwordResetStatus = MutableLiveData<Resource<String>>()
    val passwordResetStatus: LiveData<Resource<String>> = _passwordResetStatus

    private val _loginObserver = PublishSubject.create<Resource<AuthResult>>()
    val loginObsevrer get() = _loginObserver.hide()

    fun register(email: String,username: String,password: String, repeatPassword: String) {
        val error = if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            applicationContext.getString(R.string.error_input_empty)
        } else if (password != repeatPassword) {
            Log.d("Password","$password $repeatPassword")
            applicationContext.getString(R.string.error_incorrectly_repeated_password)
        } else if (username.length < MIN_USERNAME_LENGHT) {
            applicationContext.getString(R.string.error_username_too_short, MIN_USERNAME_LENGHT)
        } else if (username.length > MAX_USERNAME_LENGHT) {
            applicationContext.getString(R.string.error_username_too_long, MAX_USERNAME_LENGHT)
        } else if (password.length < MIN_PASSWORD_LENGHT) {
            applicationContext.getString(R.string.error_password_too_short, MIN_PASSWORD_LENGHT)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            applicationContext.getString(R.string.error_not_a_valid_email)
        }
        else null

        error?.let {
            _registerStatus.postValue(Resource.Error(it))
            return
        }
        _registerStatus.postValue(Resource.Loading())
        viewModelScope.launch(dispatcher) {
            val result = repository.register(email, username, password)
            _registerStatus.postValue(result)
        }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            val error = applicationContext.getString(R.string.error_input_empty)
            _loginStatus.postValue(Resource.Error(error))
        } else {
            viewModelScope.launch(dispatcher) {
                val result = repository.login(email,password)
                _loginStatus.postValue(result)
            }
        }
    }

    fun loginWithGoogle(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken,null)
        viewModelScope.launch(dispatcher) {
            val result = repository.loginWithGoogle(credentials)
            _loginStatus.postValue(result)
        }
    }

    fun resetPassword(email: String) {
        if (!email.isEmpty()) {
            viewModelScope.launch(dispatcher) {
                repository.resetPassword(email)
                _passwordResetStatus.postValue(Resource.Success(applicationContext.getString(R.string.go_to_email)))
            }
        } else {
            _passwordResetStatus.postValue(Resource.Success(applicationContext.getString(R.string.error_input_empty)))
        }
    }
    fun resetPasswordRx(email: String) {
        if (!email.isEmpty()) {
            repository.restPasswordRx(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _passwordResetStatus.postValue(Resource.Success("Password reset link was sent to your email"))
                }, {
                    _passwordResetStatus.postValue(Resource.Error(it.message ?: ""))
                })
        } else {
            _passwordResetStatus.postValue(Resource.Error(applicationContext.getString(R.string.error_input_empty)))
        }
    }

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
               // _loginObserver.onNext(Resource.Success(it))
            }, {
                _loginStatus.postValue(Resource.Error(it.message ?: ""))
            })
    }

    fun registerRx(email: String,username: String,password: String, repeatPassword: String) {
        val error = if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            applicationContext.getString(R.string.error_input_empty)
        } else if (password != repeatPassword) {
            applicationContext.getString(R.string.error_incorrectly_repeated_password)
        } else if (username.length < MIN_USERNAME_LENGHT) {
            applicationContext.getString(R.string.error_username_too_short, MIN_USERNAME_LENGHT)
        } else if (username.length > MAX_USERNAME_LENGHT) {
            applicationContext.getString(R.string.error_username_too_long, MAX_USERNAME_LENGHT)
        } else if (password.length < MIN_PASSWORD_LENGHT) {
            applicationContext.getString(R.string.error_password_too_short, MIN_PASSWORD_LENGHT)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            applicationContext.getString(R.string.error_not_a_valid_email)
        }
        else null

        error?.let {
            _registerStatus.postValue(Resource.Error(it))
            return
        }
        _registerStatus.postValue(Resource.Loading())
        repository.registerRx(email, username, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _registerStatus.postValue(Resource.Success(it))
            }, {
                _registerStatus.postValue(Resource.Error(it.message ?: ""))
            })
    }
}




















