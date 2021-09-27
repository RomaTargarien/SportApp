package com.example.sportapp.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.text.Html
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.example.sportapp.R
import com.example.sportapp.models.rss.materials.Item
import com.example.sportapp.other.Constants.CHANNEL_ID
import com.example.sportapp.other.Constants.CHANNEL_NAME
import com.example.sportapp.other.Constants.NOTIFICATION_ID
import com.example.sportapp.other.ext.itemsToInsert
import com.example.sportapp.repositories.main.DefaultMainRepository
import com.example.sportapp.repositories.main.MainApiRepository
import com.example.sportapp.ui.main.MainActivity
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MaterialsService : Service() {

    private val update = PublishSubject.create<Unit>()
    private lateinit var pendingIntent: PendingIntent
    private lateinit var disposable: Disposable
    private var imageDone = PublishSubject.create<Bitmap>()
    private val showNotification = PublishSubject.create<Notification>()

    @Inject
    lateinit var mainApiRepository: MainApiRepository

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val intentMain = MainActivity.newIntent(this)
        pendingIntent = PendingIntent.getActivity(this,0,intentMain,0)

        disposable = Observable.interval(20,TimeUnit.SECONDS)
            .doOnNext {
                Log.d("TAG","service refresh")
                update.onNext(Unit)
            }
            .retry()
            .subscribe()

        return START_STICKY
    }

    init {
        update.observeOn(Schedulers.io())
            .switchMapSingle {
                mainApiRepository.getApiMaterials()
        }
            .map {
                it.channel.items
            }
            .map {
                it.itemsToInsert(mainApiRepository.fetchWithOffset(0,limit = it.size))
            }
            .doOnNext {
                if (it.isNotEmpty()) {
                    mainApiRepository.insertDataInDatabase(it)
                }
            }
            .doOnError {
                Log.d("TAG",it.localizedMessage)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .retry()
            .subscribe({
                if (it.isNotEmpty()) {
                    sendNotification(it)
                }
            },{})

        showNotification.subscribe({
            with(NotificationManagerCompat.from(this)) {
                notify(NOTIFICATION_ID,it)
            }
        },{})
    }

    private fun sendNotification(items: List<Item>) {
        createNotificationChannel()
        var notification: Notification
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ball)

        when (items.size) {
            1 -> {
                Picasso.with(this).load(items[0].enclosure.url).into(object : com.squareup.picasso.Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        Log.d("TAG",bitmap.toString())
                        imageDone.onNext(bitmap)
                    }

                    override fun onBitmapFailed(errorDrawable: Drawable?) {
                        Log.d("TAG",errorDrawable.toString())
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                })

                imageDone.subscribe({
                    notification = builder.setContentTitle(items[0].title)
                        .setContentText(Html.fromHtml(Html.fromHtml(items[0].description).toString()))
                        .setLargeIcon(it)
                        .build()
                    showNotification.onNext(notification)
                },{})
            }
            else -> {
                notification = builder
                    .setContentTitle("У вас ${items.size} непрочитанные новости")
                    .build()
                showNotification.onNext(notification)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT).apply {
                lightColor = Color.BLACK
                enableLights(true)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

}












