package com.owenlejeune.tvtime.api

import retrofit2.Converter

interface Converter {

    fun get(): Converter.Factory

}