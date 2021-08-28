package com.example.sportapp.other

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.sportapp.R
import com.example.sportapp.databinding.ProgressScreenBinding
import com.example.sportapp.other.ext.forEachChildView
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar

fun Fragment.progressScreen(
    progressScreenBinding: ProgressScreenBinding,
    progressScreenBehavior: Resource<String>,
    animation : Animation,
    layoutToDisable: ConstraintLayout,
    cardViewTopMargin: Int = 260
) {
    val params: ViewGroup.MarginLayoutParams = progressScreenBinding.cardError.layoutParams as ViewGroup.MarginLayoutParams
    params.topMargin = cardViewTopMargin.toDp(this.requireContext())
    if (progressScreenBehavior is Resource.Error) {
        progressScreenViewsVisibility(cardViewVisibility = true,progressBarVisibility = false
            ,errorTextVisibility = true,progressScreenBinding = progressScreenBinding)
        progressScreenBinding.cardError.startAnimation(animation)
        progressScreenBinding.lottieResultLogIn.setAnimation(R.raw.error)
        progressScreenBinding.lottieResultLogIn.frame = 0
        progressScreenBinding.lottieResultLogIn.playAnimation()
        progressScreenBinding.lottieResultLogIn.isVisible = true
        progressScreenBinding.errorText.text = progressScreenBehavior.message
    }
    if (progressScreenBehavior is Resource.Loading) {
        progressScreenViewsVisibility(progressBarVisibility = true, cardViewVisibility = false,
            errorTextVisibility = false, lottieResultVisibility = false,progressScreenBinding = progressScreenBinding )
        progressScreenBinding.progressScreen.isVisible = true
        layoutToDisable.forEachChildView { it.isEnabled = false }
    }
    if (progressScreenBehavior is Resource.Success) {
        progressScreenBinding.progressScreen.isVisible = false
        layoutToDisable.forEachChildView { it.isEnabled = true }
    }
    if (progressScreenBehavior is Resource.EmailSuccess) {
        progressScreenViewsVisibility(cardViewVisibility = true,progressBarVisibility = false
            ,errorTextVisibility = true,progressScreenBinding = progressScreenBinding)
        progressScreenBinding.cardError.startAnimation(animation)
        progressScreenBinding.lottieResultLogIn.setAnimation(R.raw.done)
        progressScreenBinding.lottieResultLogIn.frame = 0
        progressScreenBinding.lottieResultLogIn.speed = 0.8f
        progressScreenBinding.lottieResultLogIn.playAnimation()
        progressScreenBinding.lottieResultLogIn.isVisible = true
        progressScreenBinding.errorText.text = progressScreenBehavior.message
    }
}

fun progressScreenViewsVisibility(
    cardViewVisibility: Boolean,
    errorTextVisibility: Boolean,
    lottieResultVisibility: Boolean = true,
    progressBarVisibility: Boolean,
    progressScreenBinding: ProgressScreenBinding

) {
    progressScreenBinding.progressBar.isVisible = progressBarVisibility
    progressScreenBinding.lottieResultLogIn.isVisible = lottieResultVisibility
    progressScreenBinding.errorText.isVisible = errorTextVisibility
    progressScreenBinding.cardError.isVisible = cardViewVisibility
}

fun Int.toDp(context: Context): Int = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics).toInt()