package com.example.sportapp.ui.main.viewModels

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.sportapp.models.rss.materials.Item
import com.example.sportapp.other.ext.convertRssQueryToCategory
import com.example.sportapp.other.ext.convertToRssQuery
import com.example.sportapp.other.states.DbState
import com.example.sportapp.other.states.ListState
import com.example.sportapp.repositories.main.MainApiRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

abstract class DataProviderViewModel(
    private var mainApiRepository: MainApiRepository
): ViewModel() {

    val refresh = PublishSubject.create<DbState>()
    val materials = BehaviorSubject.create<MutableList<Item>>()
    val changeTheOffset = PublishSubject.create<Unit>()
    val smoothScrollToFirstPosition = PublishSubject.create<Unit>()
    val getDataWithOffset = BehaviorSubject.create<ListState>()
    val isLastItems = BehaviorSubject.createDefault(false)

    init {

        refresh
            .observeOn(Schedulers.io())
            .switchMapSingle { dbState ->
                Log.d("TAG",dbState.rssQuery)
                mainApiRepository.getApiMaterials(dbState.rssQuery).map {
                    Pair(dbState,it.channel.items)
                }
            }
            .map { (dbState,items) ->
                val category = dbState.rssQuery.convertRssQueryToCategory()
                if (!(dbState is DbState.Empty)) {
                    val lastItems = mainApiRepository.fetchWithOffset(0,category = category)
                    Pair(itemsToInsert(items,lastItems),category)
                } else {
                    Pair(items,category)
                }
            }
            .doOnNext {
                mainApiRepository.insertDataInDatabase(it.first)
            }
            .doOnNext { (list,category) ->
                if (!list.isEmpty()) {
                    getDataWithOffset.onNext(ListState.Reload(category))
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()


        getDataWithOffset.observeOn(Schedulers.io())
            .map {
                when (it) {
                    is ListState.Fulled -> {
                        val list = mutableListOf<Item>()
                        if (materials.hasValue()) {
                            list.addAll(materials.value)
                        }
                        Log.d("TAG",it.category)
                        val items = mainApiRepository.fetchWithOffset(it.offset,category = it.category)
                        Log.d("TAG",items.size.toString())
                        if ((items.isEmpty() || items.size < 20) && it.offset == 0) {
                            refresh.onNext(DbState.Empty(it.category.convertToRssQuery()))
                        } else if (items.isEmpty()) {
                            isLastItems.onNext(true)
                        }
                        else {
                            list.addAll(items)
                        }
                        Pair(list,it)
                    }
                    is ListState.Reload -> {
                        changeTheOffset.onNext(Unit)
                        isLastItems.onNext(false)
                        val list = mainApiRepository.fetchWithOffset(0,category = it.category)
                        Pair(list,it)
                    }
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                materials.onNext(it.first.toMutableList())
                if (it.second is ListState.Reload) {
                    smoothScrollToFirstPosition.onNext(Unit)
                }
            },{})
    }

    private fun itemsToInsert(newItems: List<Item>, lastItems: List<Item>): List<Item> {
        val itemsToInsert = mutableListOf<Item>()
        for (item in newItems) {
            if (!(item in lastItems)) {
                itemsToInsert.add(item)
            } else {
                break
            }
        }
        return itemsToInsert
    }
}