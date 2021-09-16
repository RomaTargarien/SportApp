package com.example.sportapp.ui.main.viewModels

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.sportapp.models.User
import com.example.sportapp.repositories.main.MainApiRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

class UserFragmentViewModel @ViewModelInject constructor(
    mainApiRepository: MainApiRepository
): ViewModel() {

    val userRealtimeUdaptes = BehaviorSubject.create<User>()
    val logOut = PublishSubject.create<Unit>()

    val verifyEmail = PublishSubject.create<Unit>()

    init {
        mainApiRepository.subscribeToRealtimeUpdates()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                userRealtimeUdaptes.onNext(it)
            },{})

        verifyEmail.observeOn(Schedulers.io())
            .switchMap {
                mainApiRepository.sendVerificationEmail()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                Log.d("TAG",it.message.toString())
            }
            .retry()
            .subscribe({
                Log.d("TAG","Email sent")
            },{})
    }
}