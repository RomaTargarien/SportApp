package com.example.sportapp.other.states

import com.example.sportapp.other.Constants

sealed class ListState(val offset: Int = Constants.OFFSET) {
    class Reload : ListState()
    class Fulled(offset: Int) : ListState(offset)
}