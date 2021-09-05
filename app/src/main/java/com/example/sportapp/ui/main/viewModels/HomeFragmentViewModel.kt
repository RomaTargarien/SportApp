package com.example.sportapp.ui.main.viewModels

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.db.NewsDao
import com.example.sportapp.models.rss.materials.Item
import com.example.sportapp.repositories.main.DefaultMainApiRepository
import com.example.sportapp.repositories.main.MainApiRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject


class HomeFragmentViewModel @ViewModelInject constructor(
   private var mainApiRepository: MainApiRepository,
   private var dao: NewsDao
): ViewModel() {

    val refresh = PublishSubject.create<Unit>()
    val materials = BehaviorSubject.create<MutableList<Item>>()
    val getData = PublishSubject.create<Unit>()
    var offset = 0
    var newComingListSize = 0
    var isEmpty = false

    init {

        refresh.observeOn(Schedulers.io())
            .switchMapSingle {
            mainApiRepository.getApiMaterials()
        }
            .map {
                it.channel.items
            }
            .map { newItems ->
                if (!isEmpty) {
                    val lastItem = dao.getItemsWithOffset(0,1).get(0)
                    itemsToInsert(newItems,lastItem)
                } else {
                    newItems
                }
            }
            .doOnNext {
                newComingListSize = it.size
                Log.d("TAG","newComingListSize $newComingListSize")
            }
            .doOnNext {
                dao.putNewItems(it)
                isEmpty = false
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()


        dao.getItemsWithOffsetObservable(0,newComingListSize)
            .doOnNext {
                Log.d("TAG","newComingListSize2 $newComingListSize")
            }
            .subscribeOn(Schedulers.io())
            .map { items ->
                Log.d("TAG","newComingListSize3 $newComingListSize")
                Log.d("TAG","itemsToAddOnTheTop - ${items.toString()}")
                val list = mutableListOf<Item>()
                list.addAll(items)
                if (materials.hasValue()) {
                    list.addAll(materials.value)
                }
                list
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                //materials.onNext(it)
            },{})

        getData.observeOn(Schedulers.io())
            .map {
                dao.getItemsWithOffset(offset)
                //mainApiRepository.fetchWithOffset(offset)
            }
            .map { items ->
                val list = mutableListOf<Item>()
                if (materials.hasValue()) {
                    list.addAll(materials.value)
                }
                list.addAll(items)
                list
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                offset += 10
                Log.d("TAG",offset.toString())
                Log.d("TAG",it.toString())
                materials.onNext(it)
            },{})
    }

    private fun itemsToInsert(newItems: List<Item>,lastItem: Item): List<Item> {
        val itemsToInsert = mutableListOf<Item>()
        for (item in newItems) {
            if (!(lastItem.equals(item))) {
                itemsToInsert.add(item)
            } else {
                break
            }
        }
        Log.d("TAG","Inserted items $itemsToInsert")
        return itemsToInsert
    }
}