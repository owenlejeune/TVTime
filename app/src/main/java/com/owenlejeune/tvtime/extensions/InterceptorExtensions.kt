package com.owenlejeune.tvtime.extensions

import com.owenlejeune.tvtime.api.QueryParam
import okhttp3.HttpUrl
import okhttp3.Request

fun HttpUrl.Builder.addQueryParams(vararg queryParams: QueryParam?): HttpUrl.Builder {
    return apply {
        queryParams.forEach { param ->
            param?.let { addQueryParameter(param.key, param.param) }
        }
    }
}