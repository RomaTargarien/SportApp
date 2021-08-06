package com.example.sportapp.other

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar

fun Fragment.snackbar(message: String) {
    Snackbar.make(requireView(),message,LENGTH_LONG).show()
}