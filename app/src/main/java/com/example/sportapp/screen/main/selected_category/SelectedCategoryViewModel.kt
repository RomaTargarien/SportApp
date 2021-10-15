package com.example.sportapp.screen.main.selected_category

import android.content.Context
import android.os.Bundle
import androidx.hilt.lifecycle.ViewModelInject
import com.example.sportapp.repositories.main.MainApiRepository
import com.example.sportapp.screen.main.base_vm.DataProviderViewModel
import io.reactivex.rxjava3.subjects.PublishSubject

class SelectedCategoryViewModel @ViewModelInject constructor(
    mainApiRepository: MainApiRepository,
    private val applicationContext: Context
): DataProviderViewModel(mainApiRepository,applicationContext) {

    val isBottomNavMenuHiden = PublishSubject.create<Boolean>()

    val goToSelectedItemScreen = PublishSubject.create<Bundle>()


}