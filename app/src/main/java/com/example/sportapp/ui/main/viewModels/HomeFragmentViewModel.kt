package com.example.sportapp.ui.main.viewModels

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sportapp.db.NewsDao
import com.example.sportapp.models.rss.materials.Item
import com.example.sportapp.other.Constants.LIMIT
import com.example.sportapp.other.Constants.OFFSET
import com.example.sportapp.other.states.DbState
import com.example.sportapp.other.states.ListState
import com.example.sportapp.repositories.main.DefaultMainRepository
import com.example.sportapp.repositories.main.MainApiRepository
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
            likedCategories.onNext(it)
         },{})
   }

}


