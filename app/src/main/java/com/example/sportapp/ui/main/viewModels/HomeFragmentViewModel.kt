package com.example.sportapp.ui.main.viewModels

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
                mainApiRepository.getApiMaterials().map {
                    Pair(dbState,it.channel.items)
                }
            }
            .map { pair ->
                if (!(pair.first is DbState.Empty)) {
                    val lastItems = mainApiRepository.fetchWithOffset(0)
                    itemsToInsert(pair.second,lastItems)
                } else {
                    pair.second
                }
            }
            .doOnNext {
               mainApiRepository.insertDataInDatabase(it)
            }
            .doOnNext {
                if (!it.isEmpty()) {
                    getDataWithOffset.onNext(ListState.Reload())
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
                        val items = mainApiRepository.fetchWithOffset(it.offset)
                        if (items.isEmpty() && it.offset == 0) {
                            refresh.onNext(DbState.Empty())
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
                        val list = mainApiRepository.fetchWithOffset(0)
                        Pair(list,it)
                    }
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                for (item in it.first) {
                    Log.d("TAG",item.title)
                }
                Log.d("TAG","--------------------------------------")
                materials.onNext(it.first.toMutableList())
                if (it.second is ListState.Reload) {
                    smoothScrollToFirstPosition.onNext(Unit)
                }
            },{})
    }

    private fun itemsToInsert(newItems: List<Item>,lastItems: List<Item>): List<Item> {
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


