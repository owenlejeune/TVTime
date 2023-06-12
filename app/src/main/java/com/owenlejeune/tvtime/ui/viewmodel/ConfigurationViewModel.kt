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

    private val service: ConfigurationApi by inject()

    val detailsConfiguration = mutableStateOf(ConfigurationDetails.Empty)
    val countriesConfiguration = mutableStateListOf<ConfigurationCountry>()
    val jobsConfiguration = mutableStateListOf<ConfigurationJob>()
    val languagesConfiguration = mutableStateListOf<ConfigurationLanguage>()
    val primaryTranslationsConfiguration = mutableStateListOf<String>()
    val timezonesConfiguration = mutableStateListOf<ConfigurationTimezone>()

    fun getConfigurations() {
        getDetailsConfiguration()
        getCountriesConfiguration()
        getJobsConfiguration()
        getLanguagesConfiguration()
        getPrimaryTranslationsConfiguration()
        getTimezonesConfiguration()
    }

    fun getDetailsConfiguration() {
        getConfiguration(
            { service.getDetailsConfiguration() },
            { detailsConfiguration.value = it }
        )
    }

    fun getCountriesConfiguration() {
        getConfiguration(
            { service.getCountriesConfiguration() },
            {
                countriesConfiguration.clear()
                countriesConfiguration.addAll(it)
            }
        )
    }

    fun getJobsConfiguration() {
        getConfiguration(
            { service.getJobsConfiguration() },
            {
                jobsConfiguration.clear()
                jobsConfiguration.addAll(it)
            }
        )
    }

    fun getLanguagesConfiguration() {
        getConfiguration(
            { service.getLanguagesConfiguration() },
            {
                languagesConfiguration.clear()
                languagesConfiguration.addAll(it)
            }
        )
    }

    fun getPrimaryTranslationsConfiguration() {
        getConfiguration(
            { service.getPrimaryTranslationsConfiguration() },
            {
                primaryTranslationsConfiguration.clear()
                primaryTranslationsConfiguration.addAll(it)
            }
        )
    }

    fun getTimezonesConfiguration() {
        getConfiguration(
            { service.getTimezonesConfiguration() },
            {
                timezonesConfiguration.clear()
                timezonesConfiguration.addAll(it)
            }
        )
    }

    private fun <T> getConfiguration(
        fetcher: suspend () -> Response<T>,
        bodyHandler: (T) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
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

}