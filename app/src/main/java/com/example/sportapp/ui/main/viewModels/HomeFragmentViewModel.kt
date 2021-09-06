package com.example.sportapp.ui.main.viewModels

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sportapp.models.rss.materials.Item
import com.example.sportapp.repositories.main.MainApiRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject


class HomeFragmentViewModel @ViewModelInject constructor(
   private var mainApiRepository: MainApiRepository
): ViewModel() {

    val refresh = PublishSubject.create<Unit>()
    val materials = BehaviorSubject.create<MutableList<Item>>()
    val getData = PublishSubject.create<Unit>()
    var offset = 0
    var isEmpty = false
    val addNewItems = PublishSubject.create<Unit>()
    var newComingListSize = 0
    val smoothScrollToFirstPosition = PublishSubject.create<Unit>()

    init {

        refresh
            .observeOn(Schedulers.io())
            .switchMapSingle {
                mainApiRepository.getApiMaterials()
            }
            .map {
                it.channel.items
            }
            .map { newItems ->
                if (!isEmpty) {
                    val lastItem = mainApiRepository.fetchWithOffset(0,1).get(0)
                    itemsToInsert(newItems,lastItem)
                } else {
                    isEmpty = false
                    newItems
                }
            }
            .doOnNext {
                newComingListSize = it.size
                offset += newComingListSize
            }
            .doOnNext {
                mainApiRepository.insertDataInDatabase(it)
            }
            .doOnNext {
                if (!it.isEmpty()) {
                    addNewItems.onNext(Unit)
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        addNewItems
            .observeOn(Schedulers.io())
            .map {
                mainApiRepository.fetchWithOffset(0,newComingListSize)
            }
            .map { items ->
                val list = mutableListOf<Item>()
                list.addAll(items)
                if (materials.hasValue()) {
                    list.addAll(materials.value)
                }
                list
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                materials.onNext(it)
                smoothScrollToFirstPosition.onNext(Unit)
            },{})

        getData
            .observeOn(Schedulers.io())
            .map {
                mainApiRepository.fetchWithOffset(offset)
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
                if (it.isEmpty()) {
                    isEmpty = true
                } else {
                    offset += 10
                    materials.onNext(it)
                }
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
        return itemsToInsert
    }
}