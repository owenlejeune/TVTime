package com.owenlejeune.tvtime.api

class QueryParam(val key: String, val param: String) {

    constructor(key: String, param: Any): this(key, param.toString())

}