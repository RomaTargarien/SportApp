package com.example.sportapp.ui.main.viewModels

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sportapp.models.rss.materials.Item
import com.example.sportapp.repositories.main.DefaultMainApiRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject


class HomeFragmentViewModel @ViewModelInject constructor(
   private var mainApiRepository: DefaultMainApiRepository
): ViewModel() {

    val refresh = PublishSubject.create<Unit>()
    val materials = BehaviorSubject.create<MutableList<Item>>()
    val getData = PublishSubject.create<Unit>()
    var offset = 0

    init {

        refresh.observeOn(Schedulers.io())
            .switchMapSingle {
            mainApiRepository.getApiMaterials()
        }
            .map {
                it.channel.items
            }
            .switchMap { newItems ->
                mainApiRepository.fetchDataFromDatabase().map {  oldItems ->
                    itemsToInsert(oldItems,newItems)
                }
            }
            .doOnNext {
                mainApiRepository.insertDataInDatabase(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()


        mainApiRepository.fetchDataFromDatabase()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                materials.onNext(it.toMutableList())
            },{})


//        getData.observeOn(Schedulers.io())
//            .switchMap {
//                mainApiRepository.fetchWithOffset(offset)
//            }
//            .map { items ->
//                offset += 10
//                Log.d("TAG","offset - $offset")
//                val list = mutableListOf<Item>()
//                if (materials.hasValue()) {
//                    list.addAll(materials.value)
//                }
//                list.addAll(items)
//                list
//            }
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({
//                materials.onNext(it.toMutableList())
//                Log.d("TAG","Room - ${it.size}")
//            },{})
    }

    private fun itemsToInsert(oldItems: List<Item>,newItems: List<Item>): List<Item> {
        val itemsToInsert = mutableListOf<Item>()
        for (item in newItems) {
            if (!(item in oldItems)) {
                itemsToInsert.add(item)
            }
        }
        Log.d("TAG","Inserted items $itemsToInsert")
        return itemsToInsert
    }
}