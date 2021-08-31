package com.example.sportapp.ui.main.viewModels

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.sportapp.apis.MaterialsApi
import com.example.sportapp.data.rss.materials.Item
import com.example.sportapp.data.rss.materials.Rss
import com.example.sportapp.db.NewsDao
import com.example.sportapp.repositories.main.DefaultMainRepository
import com.example.sportapp.repositories.main.MainRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject


class HomeFragmentViewModel @ViewModelInject constructor(
    mainRepository: MainRepository,
    newsDao: NewsDao
): ViewModel() {


    init {
        mainRepository.getMaterials().toObservable()
            .subscribeOn(Schedulers.io())
            .map {
                it.channel.items
            }
            .observeOn(Schedulers.io())
            .doOnNext {
                Log.d("TAG",Thread.currentThread().toString())
                //newsDao.putNewItems(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                //Log.d("TAG",it.toString())
            },{})
        Observable.create { emiter: ObservableEmitter<Unit> ->
            emiter.onNext(Unit)
        }
            .observeOn(Schedulers.computation())
            .doOnNext {
                Log.d("TAG",newsDao.getAllNews().toString())
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

    }
}