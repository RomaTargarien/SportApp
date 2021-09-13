package com.example.sportapp.other.states

import com.example.sportapp.other.Constants

sealed class ListState(val offset: Int = Constants.OFFSET,val category: String) {
    class Reload(category: String = "%") : ListState(category = category)
    class Fulled(offset: Int,category: String = "%") : ListState(offset,category)
}