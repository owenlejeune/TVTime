package com.owenlejeune.tvtime.api.tmdb.api.v3

import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ConfigurationCountry
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ConfigurationDetails
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ConfigurationJob
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ConfigurationLanguage
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ConfigurationTimezone
import retrofit2.Response
import retrofit2.http.GET

interface ConfigurationApi {

    @GET("configuration")
    suspend fun getDetailsConfiguration(): Response<ConfigurationDetails>

    @GET("configuration/countries")
    suspend fun getCountriesConfiguration(): Response<List<ConfigurationCountry>>

    @GET("configuration/jobs")
    suspend fun getJobsConfiguration(): Response<List<ConfigurationJob>>

    @GET("configuration/languages")
    suspend fun getLanguagesConfiguration(): Response<List<ConfigurationLanguage>>

    @GET("configuration/primary_translations")
    suspend fun getPrimaryTranslationsConfiguration(): Response<List<String>>

    @GET("configuration/timezones")
    suspend fun getTimezonesConfiguration(): Response<List<ConfigurationTimezone>>

}