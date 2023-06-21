package com.owenlejeune.tvtime.api

import retrofit2.Response

infix fun <T> Response<T>.storedIn(body: (T) -> Unit) {
    if (isSuccessful) {
        body()?.let {
            body(it)
        }
    }
}