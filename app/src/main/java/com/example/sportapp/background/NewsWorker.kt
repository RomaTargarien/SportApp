package com.example.sportapp.background

import android.content.Context
import android.util.Log
import com.example.sportapp.models.rss.materials.Item
import com.example.sportapp.repositories.main.MainApiRepository


//class NewsWorker(
//    val context: Context,
//    workerParams: WorkerParameters,
//    val mainApiRepository: MainApiRepository
//) : Worker(context, workerParams) {
//
//
//    override fun doWork(): Result {
//        val olItems = mainApiRepository.fetchWithOffset(0)
//        Log.d("TAG","WORKER")
//        mainApiRepository.getApiMaterials()
//            .toObservable()
//            .map {
//                it.channel.items
//            }
//            .map {
//                Log.d("TAG","newItems")
//                itemsToInsert(it,olItems)
//            }
//            .doOnNext {
//                if (!it.isEmpty()) {
//                    mainApiRepository.insertDataInDatabase(it)
//                }
//            }
//            .subscribe()
//
//        return Result.success()
//    }
//
//    private fun itemsToInsert(newItems: List<Item>, lastItems: List<Item>): List<Item> {
//        val itemsToInsert = mutableListOf<Item>()
//        for (item in newItems) {
//            if (!(item in lastItems)) {
//                itemsToInsert.add(item)
//            } else {
//                break
//            }
//        }
//        return itemsToInsert
//    }
//
//}