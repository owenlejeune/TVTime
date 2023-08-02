package com.owenlejeune.tvtime.extensions

import com.owenlejeune.tvtime.api.common.QueryParam
import okhttp3.HttpUrl

fun HttpUrl.Builder.addQueryParams(vararg queryParams: QueryParam?): HttpUrl.Builder {
    return apply {
        queryParams.forEach { param ->
            param?.let { addQueryParameter(param.key, param.param) }
        }
    }
}