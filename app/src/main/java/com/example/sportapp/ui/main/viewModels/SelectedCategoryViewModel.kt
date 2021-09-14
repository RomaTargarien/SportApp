package com.example.sportapp.ui.main.viewModels

import android.os.Bundle
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.sportapp.models.rss.materials.Item
import com.example.sportapp.other.ext.convertRssQueryToCategory
import com.example.sportapp.other.ext.convertToRssQuery
import com.example.sportapp.other.states.DbState
import com.example.sportapp.other.states.ListState
import com.example.sportapp.repositories.main.MainApiRepository
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

class SelectedCategoryViewModel @ViewModelInject constructor(
    mainApiRepository: MainApiRepository
): DataProviderViewModel(mainApiRepository) {

    val isBottomNavMenuHiden = PublishSubject.create<Boolean>()

    val goToSelectedItemScreen = PublishSubject.create<Bundle>()


}