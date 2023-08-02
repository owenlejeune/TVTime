package com.owenlejeune.tvtime.api.common

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateTypeAdapter: TypeAdapter<Date>() {

    companion object {
        private val acceptedDateFormats: List<String> = listOf(
            "yyyy-MM-dd",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        )
    }

    override fun read(jrIn: JsonReader): Date? {
        if (jrIn.peek() == JsonToken.NULL) {
            jrIn.nextNull()
            return null
        }
        val dateFields = jrIn.nextString()
        if (dateFields.isEmpty()) {
            return null
        }
        for (dateFormat in acceptedDateFormats) {
            try {
                val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
                return formatter.parse(dateFields) ?: throw Exception("Parsed date cannot be null")
            } catch (e: Exception) {
                continue
            }
        }
        throw Exception("No accepted date format to match date string \"$dateFields\"")
    }

    override fun write(jrOut: JsonWriter, value: Date?) {
        value?.let {
            jrOut.value(it.toString())
        } ?: run {
            jrOut.nullValue()
        }
    }

}