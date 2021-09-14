package com.example.sportapp.background

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.sportapp.models.rss.materials.Item
import com.example.sportapp.repositories.main.MainApiRepository
import javax.inject.Inject


class NewsWorker (
    val context: Context,
    val workerParams: WorkerParameters,
) : Worker(context, workerParams) {



    override fun doWork(): Result {
        Log.d("TAG","WORKER")

        return Result.success()
    }


}