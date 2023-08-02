package com.owenlejeune.tvtime.api.common

class QueryParam(val key: String, val param: String) {

    constructor(key: String, param: Any): this(key, param.toString())

}