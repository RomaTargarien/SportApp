package com.example.sportapp.ui.main.viewModels

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.sportapp.other.states.Resource
import com.example.sportapp.other.validateEmail
import com.example.sportapp.other.validatePassword
import com.example.sportapp.repositories.main.MainApiRepository
import com.example.sportapp.ui.main.viewModels.base.ReauthenticationViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class EmailChangeViewModel @ViewModelInject constructor(
    mainApiRepository: MainApiRepository,
    private val applicationContext: Context
): ReauthenticationViewModel(mainApiRepository, applicationContext) {


    val emailChahge = PublishSubject.create<Unit>()
    val errorMessage = BehaviorSubject.create<String>()

    val _newEmail = BehaviorSubject.create<String>()
    val newEmail = BehaviorSubject.create<Resource<String>>()
    val tvEndEnabled = BehaviorSubject.createDefault(false)



    init {

        val newEmailSubject = _newEmail
            .distinctUntilChanged()
            .doOnNext {
                tvEndEnabled.onNext(false)
            }
            .debounce(1000,TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.io())
            .map {
                Pair(it,it.validateEmail(applicationContext))
            }
            .observeOn(AndroidSchedulers.mainThread())
            .cacheWithInitialCapacity(1)

        newEmailSubject.subscribe({
           newEmail.onNext(it.second)
        },{})

        newEmailSubject.subscribe({
            tvEndEnabled.onNext(it.second is Resource.Success)
        },{})

        emailChahge
            .withLatestFrom(newEmailSubject) {_,email -> email.first}
            .doOnNext {
                isProgressBarVisible.onNext(true)
            }
            .observeOn(Schedulers.io())
            .switchMap {
                mainApiRepository.updateEmail(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                isProgressBarVisible.onNext(false)
                errorMessage.onNext(it.localizedMessage)
            }
            .retry()
            .subscribe({
                isProgressBarVisible.onNext(false)
                goToUserScreen.onNext("Логин успешно изменен")
            },{})
    }
}