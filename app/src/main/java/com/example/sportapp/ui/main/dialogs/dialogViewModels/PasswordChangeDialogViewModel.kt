package com.example.sportapp.ui.main.dialogs.dialogViewModels

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.sportapp.repositories.main.MainApiRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject


class PasswordChangeDialogViewModel @ViewModelInject constructor(
    mainApiRepository: MainApiRepository
): ViewModel() {

    val changePassword = PublishSubject.create<Unit>()
    val passwordHasChanged = PublishSubject.create<Unit>()

    init {
        changePassword.observeOn(Schedulers.io())
            .switchMap {
                mainApiRepository.changePassword()
            }
            .doOnError {
                Log.d("TAG",it.localizedMessage.toString())
            }
            .retry()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                passwordHasChanged.onNext(Unit)
            },{})
    }
}