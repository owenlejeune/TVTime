package com.owenlejeune.tvtime.extensions

import com.owenlejeune.tvtime.api.QueryParam
import okhttp3.Interceptor
import okhttp3.Request

fun Interceptor.Chain.addQueryParams(vararg queryParams: QueryParam?): Request {
    val original = request()
    val originalHttpUrl = original.url()

    val urlBuilder = originalHttpUrl.newBuilder()
    queryParams.forEach { param ->
        if (param != null) {
            urlBuilder.addQueryParameter(param.key, param.param)
        }
    }
    val url = urlBuilder.build()

    val requestBuilder = original.newBuilder()
        .url(url)

    return requestBuilder.build()
}