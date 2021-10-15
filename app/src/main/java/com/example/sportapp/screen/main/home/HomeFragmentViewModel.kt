package com.example.sportapp.screen.main.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import com.example.sportapp.repositories.main.MainApiRepository
import com.example.sportapp.screen.main.base_vm.DataProviderViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject


class HomeFragmentViewModel @ViewModelInject constructor(
   mainApiRepository: MainApiRepository,
   private val applicationContext: Context
): DataProviderViewModel(mainApiRepository,applicationContext) {

   val likedCategories = BehaviorSubject.create<List<String>>()
   val updateLikedCategories = BehaviorSubject.create<String>()

   val goToSelectedCategoryScreen = PublishSubject.create<Bundle>()
   val goToSelectedItemScreen = PublishSubject.create<Bundle>()

   init {
      Log.d("TAG","init homeViewModel")
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


