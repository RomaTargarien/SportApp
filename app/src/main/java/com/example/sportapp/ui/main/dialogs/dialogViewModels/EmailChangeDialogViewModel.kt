package com.example.sportapp.ui.main.dialogs.dialogViewModels

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.sportapp.repositories.main.MainApiRepository
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.lang.StringBuilder

class EmailChangeDialogViewModel @ViewModelInject constructor(
    mainApiRepository: MainApiRepository
): ViewModel() {

    val emailChahge = PublishSubject.create<String>()
    val emailHasChanged = PublishSubject.create<Unit>()

    init {
        emailChahge.observeOn(Schedulers.io())
            .switchMap {
                mainApiRepository.updateEmail(it)
            }
            .doOnError {
                Log.d("TAG",it.message.toString())
            }
            .retry()
            .subscribe({
                emailHasChanged.onNext(Unit)
            },{})
    }
}