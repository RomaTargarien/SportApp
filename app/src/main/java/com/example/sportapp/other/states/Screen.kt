package com.example.sportapp.other.states

sealed class Screen {
    class Home: Screen()
    class SelectedCategory: Screen()
}
