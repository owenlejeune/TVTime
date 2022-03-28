package com.owenlejeune.tvtime.api

import retrofit2.Converter

interface ConverterFactoryFactory {

    fun get(): Converter.Factory

}