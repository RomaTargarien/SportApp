package com.example.sportapp.other.states

sealed class DbState {
    class Empty : DbState()
    class Fulled : DbState()
}
