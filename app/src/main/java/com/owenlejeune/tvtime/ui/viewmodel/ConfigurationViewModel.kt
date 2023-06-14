package com.owenlejeune.tvtime.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.owenlejeune.tvtime.api.tmdb.api.v3.ConfigurationApi
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ConfigurationCountry
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ConfigurationDetails
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ConfigurationJob
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ConfigurationLanguage
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ConfigurationTimezone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Response

class ConfigurationViewModel: ViewModel(), KoinComponent {

    companion object {
        private const val TAG = "ConfigurationViewModel"
    }

    private object Backer {
        val detailsConfiguration = mutableStateOf(ConfigurationDetails.Empty)
        val countriesConfiguration = mutableStateListOf<ConfigurationCountry>()
        val jobsConfiguration = mutableStateListOf<ConfigurationJob>()
        val languagesConfiguration = mutableStateListOf<ConfigurationLanguage>()
        val primaryTranslationsConfiguration = mutableStateListOf<String>()
        val timezonesConfiguration = mutableStateListOf<ConfigurationTimezone>()
    }

    private val service: ConfigurationApi by inject()

    val detailsConfiguration = Backer.detailsConfiguration
    val countriesConfiguration = Backer.countriesConfiguration
    val jobsConfiguration = Backer.jobsConfiguration
    val languagesConfiguration = Backer.languagesConfiguration
    val primaryTranslationsConfiguration = Backer.primaryTranslationsConfiguration
    val timezonesConfiguration = Backer.timezonesConfiguration

    suspend fun getConfigurations() {
        getDetailsConfiguration()
        getCountriesConfiguration()
        getJobsConfiguration()
        getLanguagesConfiguration()
        getPrimaryTranslationsConfiguration()
        getTimezonesConfiguration()
    }

    suspend fun getDetailsConfiguration() {
        getConfiguration(
            { service.getDetailsConfiguration() },
            { detailsConfiguration.value = it }
        )
    }

    suspend fun getCountriesConfiguration() {
        getConfiguration(
            { service.getCountriesConfiguration() },
            {
                countriesConfiguration.clear()
                countriesConfiguration.addAll(it)
            }
        )
    }

    suspend fun getJobsConfiguration() {
        getConfiguration(
            { service.getJobsConfiguration() },
            {
                jobsConfiguration.clear()
                jobsConfiguration.addAll(it)
            }
        )
    }

    suspend fun getLanguagesConfiguration() {
        getConfiguration(
            { service.getLanguagesConfiguration() },
            {
                languagesConfiguration.clear()
                languagesConfiguration.addAll(it)
            }
        )
    }

    suspend fun getPrimaryTranslationsConfiguration() {
        getConfiguration(
            { service.getPrimaryTranslationsConfiguration() },
            {
                primaryTranslationsConfiguration.clear()
                primaryTranslationsConfiguration.addAll(it)
            }
        )
    }

    suspend fun getTimezonesConfiguration() {
        getConfiguration(
            { service.getTimezonesConfiguration() },
            {
                timezonesConfiguration.clear()
                timezonesConfiguration.addAll(it)
            }
        )
    }

    private suspend fun <T> getConfiguration(
        fetcher: suspend () -> Response<T>,
        bodyHandler: (T) -> Unit
    ) {
        val response = fetcher()
        if (response.isSuccessful) {
            response.body()?.let {
                Log.d(TAG, "Successfully got configuration: $it")
                bodyHandler(it)
            }
        } else {
            Log.e(TAG, "Issue getting configuration")
        }
    }

}