package com.example.sportapp.screen.main.dialogs.email_verification

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.sportapp.repositories.main.MainApiRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject

class EmailVerifyingViewModel @ViewModelInject constructor(
    mainApiRepository: MainApiRepository
): ViewModel() {

    val emailVerify = PublishSubject.create<Unit>()
    val verifyingWasSent = PublishSubject.create<Unit>()

    init {
        emailVerify.observeOn(Schedulers.io())
            .switchMap {
                mainApiRepository.sendVerificationEmail()
            }
            .doOnError {
                Log.d("TAG",it.localizedMessage.toString())
            }
            .retry()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                verifyingWasSent.onNext(Unit)
            },{})
    }
}