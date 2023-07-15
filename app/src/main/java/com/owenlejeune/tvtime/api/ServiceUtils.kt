package com.owenlejeune.tvtime.api

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.delay
import retrofit2.Response

infix fun <T> Response<T>.storedIn(body: (T) -> Unit) {
    if (isSuccessful) {
        body()?.let {
            body(it)
        }
    }
}

suspend fun <T> loadRemoteData(
    fetcher: suspend () -> Response<T>,
    processor: (T) -> Unit,
    loadSate: MutableState<LoadingState>,
    refreshing: Boolean
) {
    loadSate.value = if (refreshing) LoadingState.REFRESHING else LoadingState.LOADING
    val response = fetcher()
    if (response.isSuccessful) {
        response.body()?.let {
            processor(it)
            loadSate.value = LoadingState.COMPLETE
        } ?: run {
            loadSate.value = LoadingState.ERROR
        }
    } else {
        loadSate.value = LoadingState.ERROR
    }
}