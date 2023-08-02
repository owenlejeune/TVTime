package com.owenlejeune.tvtime.api.common

import retrofit2.Converter

interface ConverterFactoryFactory {

    fun get(): Converter.Factory

}