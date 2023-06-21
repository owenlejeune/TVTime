package com.owenlejeune.tvtime.api.tmdb.api.v3

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ConfigurationCountry
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ConfigurationDetails
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ConfigurationJob
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ConfigurationLanguage
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ConfigurationTimezone
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ConfigurationService: KoinComponent {

    companion object {
        private const val TAG = "ConfigurationService"
    }

    private val service: ConfigurationApi by inject()

    val detailsConfiguration = mutableStateOf(ConfigurationDetails.Empty)
    val countriesConfiguration = mutableStateListOf<ConfigurationCountry>()
    val jobsConfiguration = mutableStateListOf<ConfigurationJob>()
    val languagesConfiguration = mutableStateListOf<ConfigurationLanguage>()
    val primaryTranslationsConfiguration = mutableStateListOf<String>()
    val timezonesConfiguration = mutableStateListOf<ConfigurationTimezone>()

    suspend fun getDetailsConfiguration() {
        val response = service.getDetailsConfiguration()
        if (response.isSuccessful) {
            response.body()?.let {
                Log.d(TAG, "Successfully got details configuration: $it")
                detailsConfiguration.value = it
            } ?: run {
                Log.w(TAG, "Problem getting details configuration")
            }
        } else {
            Log.e(TAG, "Issue getting details configuration")
        }
    }

    suspend fun getCountriesConfiguration() {
        val response = service.getCountriesConfiguration()
        if (response.isSuccessful) {
            response.body()?.let {
                Log.d(TAG, "Successfully got countries configuration: $it")
                countriesConfiguration.clear()
                countriesConfiguration.addAll(it)
            } ?: run {
                Log.w(TAG, "Problem getting countries configuration")
            }
        } else {
            Log.e(TAG, "Issue getting counties configuration")
        }
    }

    suspend fun getJobsConfiguration() {
        val response = service.getJobsConfiguration()
        if (response.isSuccessful) {
            response.body()?.let {
                Log.d(TAG, "Successfully got jobs configuration: $it")
                jobsConfiguration.clear()
                jobsConfiguration.addAll(it)
            } ?: run {
                Log.w(TAG, "Problem getting jobs configuration")
            }
        } else {
            Log.e(TAG, "Issue getting jobs configuration")
        }
    }

    suspend fun getLanguagesConfiguration() {
        val response = service.getLanguagesConfiguration()
        if (response.isSuccessful) {
            response.body()?.let {
                Log.d(TAG, "Successfully got languages configuration: $it")
                languagesConfiguration.clear()
                languagesConfiguration.addAll(it)
            } ?: run {
                Log.w(TAG, "Problem getting languages configuration")
            }
        } else {
            Log.e(TAG, "Issue getting languages configuration")
        }
    }

    suspend fun getPrimaryTranslationsConfiguration() {
        val response = service.getPrimaryTranslationsConfiguration()
        if (response.isSuccessful) {
            response.body()?.let {
                Log.d(TAG, "Successfully got translations configuration: $it")
                primaryTranslationsConfiguration.clear()
                primaryTranslationsConfiguration.addAll(it)
            } ?: run {
                Log.w(TAG, "Problem getting translations configuration")
            }
        } else {
            Log.e(TAG, "Issue getting translations configuration")
        }
    }

    suspend fun getTimezonesConfiguration() {
        val response = service.getTimezonesConfiguration()
        if (response.isSuccessful) {
            response.body()?.let {
                Log.d(TAG, "Successfully got timezone configuration: $it")
                timezonesConfiguration.clear()
                timezonesConfiguration.addAll(it)
            } ?: run {
                Log.w(TAG, "Problem getting timezone configuration")
            }
        } else {
            Log.e(TAG, "Issue getting timezone configuration")
        }
    }

}