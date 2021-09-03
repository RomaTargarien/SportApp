package com.example.sportapp.db

import com.tickaroo.tikxml.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

class DateConverter : TypeConverter<Date> {

    private val formatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ssZ")

    override fun read(value: String?): Date {
        return formatter.parse(value)
    }

    override fun write(value: Date?): String {
        return formatter.format(value)
    }
}