package com.example.sportapp.db

import android.util.Log
import com.tickaroo.tikxml.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

class DateConverter : TypeConverter<Date> {

    private val formatter = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US)

    override fun read(value: String?): Date {
        Log.d("TAG",value.toString())
        Log.d("TAG",formatter.toPattern())
        return formatter.parse(value)
    }

    override fun write(value: Date?): String {
        return formatter.format(value)
    }
}