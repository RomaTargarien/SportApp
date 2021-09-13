package com.example.sportapp.ui.main.viewModels

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.sportapp.repositories.main.MainApiRepository
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject

class CategoriesViewModel @ViewModelInject constructor(
    mainApiRepository: MainApiRepository
): ViewModel() {

    val updateLikedCategories = BehaviorSubject.create<List<String>>()
    val likedCategories = BehaviorSubject.create<List<String>>()

    init {

        updateLikedCategories.observeOn(Schedulers.io())
            .switchMapSingle {
                mainApiRepository.updateUsersLikedCategories(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        mainApiRepository.subscribeToRealtimeUpdates()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                likedCategories.onNext(it)
            },{})
    }
}