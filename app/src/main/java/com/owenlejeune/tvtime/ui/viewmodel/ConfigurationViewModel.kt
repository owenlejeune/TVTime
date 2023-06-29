package com.owenlejeune.tvtime.ui.viewmodel

import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owenlejeune.tvtime.api.tmdb.api.v3.ConfigurationService
import com.owenlejeune.tvtime.utils.NetworkConnectivityService
import com.owenlejeune.tvtime.utils.NetworkStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ConfigurationViewModel: ViewModel(), KoinComponent {

    private val service: ConfigurationService by inject()
    private val networkConnectivityService: NetworkConnectivityService by inject()

    companion object Storage {
        val lastResponseCode = mutableIntStateOf(0)
    }

    val detailsConfiguration = service.detailsConfiguration
    val countriesConfiguration = service.countriesConfiguration
    val jobsConfiguration = service.jobsConfiguration
    val languagesConfiguration = service.languagesConfiguration
    val primaryTranslationsConfiguration = service.primaryTranslationsConfiguration
    val timezonesConfiguration = service.timezonesConfiguration

    val lastResponseCode = Storage.lastResponseCode

    val networkStatus: StateFlow<NetworkStatus> = networkConnectivityService.networkStatus.stateIn(
        initialValue = NetworkStatus.Unknown,
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000)
    )

    suspend fun getConfigurations() {
        service.getDetailsConfiguration()
        service.getCountriesConfiguration()
        service.getJobsConfiguration()
        service.getLanguagesConfiguration()
        service.getPrimaryTranslationsConfiguration()
        service.getTimezonesConfiguration()
    }

}