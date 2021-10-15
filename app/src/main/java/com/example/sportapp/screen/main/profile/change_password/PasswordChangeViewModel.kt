package com.example.sportapp.screen.main.profile.change_password

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import com.example.sportapp.other.states.Resource
import com.example.sportapp.other.ValidationImpl.Companion.PasswordValidation
import com.example.sportapp.repositories.main.MainApiRepository
import com.example.sportapp.screen.main.base_vm.ReauthenticationViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class PasswordChangeViewModel @ViewModelInject constructor(
    mainApiRepository: MainApiRepository,
    private val passwordValidation: PasswordValidation,
    private val applicationContext: Context
): ReauthenticationViewModel(mainApiRepository,passwordValidation) {

    val _newPassword = BehaviorSubject.create<String>()
    val newPassword = BehaviorSubject.create<Resource<String>>()
    val tvEndEnabled = BehaviorSubject.createDefault(false)

    val passwordChahge = PublishSubject.create<Unit>()
    val errorMessage = BehaviorSubject.create<String>()

    init {
        val newPasswordSubject = _newPassword
            .distinctUntilChanged()
            .doOnNext {
                tvEndEnabled.onNext(false)
            }
            .debounce(1000,TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.io())
            .map {
                Pair(it,passwordValidation.validate(it))
            }
            .observeOn(AndroidSchedulers.mainThread())
            .cacheWithInitialCapacity(1)

        newPasswordSubject.subscribe({
            newPassword.onNext(it.second)
        },{})

        newPasswordSubject.subscribe({
            tvEndEnabled.onNext(it.second is Resource.Success)
        },{})

        passwordChahge
            .withLatestFrom(newPasswordSubject) {_,password -> password.first}
            .doOnNext {
                isProgressBarVisible.onNext(true)
            }
            .observeOn(Schedulers.io())
            .switchMap {
                mainApiRepository.updatePassword(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                isProgressBarVisible.onNext(false)
                errorMessage.onNext(it.localizedMessage)
            }
            .retry()
            .subscribe({
                _newPassword.onNext("")
                isProgressBarVisible.onNext(false)
                goToUserScreen.onNext("Пароль успешно изменен")
            },{})
    }
}