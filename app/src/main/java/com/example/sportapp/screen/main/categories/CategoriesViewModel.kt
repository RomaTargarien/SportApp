package com.example.sportapp.screen.main.categories

import android.os.Bundle
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.sportapp.repositories.main.MainApiRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

class CategoriesViewModel @ViewModelInject constructor(
    mainApiRepository: MainApiRepository
): ViewModel() {

    val updateLikedCategories = BehaviorSubject.create<String>()
    val likedCategories = BehaviorSubject.create<List<String>>()

    val goToSelectedCategoryScreen = PublishSubject.create<Bundle>()

    init {

        updateLikedCategories.observeOn(Schedulers.io())
            .map {
                val list = mutableListOf<String>()
                if (likedCategories.hasValue()) {
                    list.addAll(likedCategories.value)
                }
                if (it in list) {
                    list.remove(it)
                } else {
                    list.add(it)
                }
                list
            }
            .switchMapSingle {
                mainApiRepository.updateUsersLikedCategories(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        mainApiRepository.subscribeToRealtimeUpdates()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                likedCategories.onNext(it.likedCategories)
            },{})
    }
}