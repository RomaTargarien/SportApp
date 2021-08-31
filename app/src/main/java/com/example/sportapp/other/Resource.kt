package com.example.sportapp.other

sealed class Resource<T>(val data: T? = null,val message: String? = null) {
    class Success<T>(data: T): Resource<T>(data)
    class Error<T>(message: String): Resource<T>(null,message)
    class Loading<T>: Resource<T>()

    class EmailSuccess<T>(message: String) : Resource<T>(null,message)
}

sealed class LoadingScreenState(val message: String? = null) {
    class Loading: LoadingScreenState()
    class Error(message: String): LoadingScreenState(message)
    class Success(message: String) :LoadingScreenState(message)
    class Invisible : LoadingScreenState()
}
