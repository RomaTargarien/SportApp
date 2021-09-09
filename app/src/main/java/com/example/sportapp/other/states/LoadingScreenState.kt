package com.example.sportapp.other.states


sealed class LoadingScreenState(val message: String? = null) {
    class Loading: LoadingScreenState()
    class Error(message: String): LoadingScreenState(message)
    class Success(message: String) :LoadingScreenState(message)
    class Invisible : LoadingScreenState()
}