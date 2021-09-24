package com.example.sportapp.ui.main.viewModels.base

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.sportapp.other.states.Resource
import com.example.sportapp.other.validatePassword
import com.example.sportapp.repositories.main.MainApiRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

abstract class ReauthenticationViewModel(
    private val mainApiRepository: MainApiRepository,
    private val applicationContext: Context
) : ViewModel() {

    val _password = BehaviorSubject.create<String>()
    val password = BehaviorSubject.create<Resource<String>>()
    val tvNextEnabled = BehaviorSubject.createDefault(false)

    val reauthenticate = PublishSubject.create<Unit>()
    val reauthenticationState = PublishSubject.create<Resource<String>>()

    val isProgressBarVisible = BehaviorSubject.createDefault(false)

    val goToUserScreen = PublishSubject.create<String>()

    init {
        val passwordSubject = _password
            .distinctUntilChanged()
            .doOnNext {
                tvNextEnabled.onNext(false)
            }
            .debounce(1000, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.io())
            .map {
                Log.d("TAG",it)
                Pair(it,it.validatePassword(applicationContext))
            }
            .observeOn(AndroidSchedulers.mainThread())
            .cacheWithInitialCapacity(1)

        passwordSubject.subscribe({
            password.onNext(it.second)
        },{})

        passwordSubject.subscribe({
            tvNextEnabled.onNext(it.second is Resource.Success)
        },{})

        reauthenticate
            .withLatestFrom(passwordSubject) {_,password -> password.first}
            .doOnNext {
                isProgressBarVisible.onNext(true)
            }
            .observeOn(Schedulers.io())
            .switchMap {
                mainApiRepository.reauthenticate(it)
            }
            .doOnError {
                isProgressBarVisible.onNext(false)
                reauthenticationState.onNext(Resource.Error(it.localizedMessage))
            }
            .retry()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _password.onNext("")
                isProgressBarVisible.onNext(false)
                reauthenticationState.onNext(Resource.Success(""))
            },{})
    }
}