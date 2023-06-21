package com.owenlejeune.tvtime.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.owenlejeune.tvtime.api.tmdb.api.v3.ConfigurationService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ConfigurationViewModel: ViewModel(), KoinComponent {

    private val service: ConfigurationService by inject()

    val detailsConfiguration = service.detailsConfiguration
    val countriesConfiguration = service.countriesConfiguration
    val jobsConfiguration = service.jobsConfiguration
    val languagesConfiguration = service.languagesConfiguration
    val primaryTranslationsConfiguration = service.primaryTranslationsConfiguration
    val timezonesConfiguration = service.timezonesConfiguration

    suspend fun getConfigurations() {
        service.getDetailsConfiguration()
        service.getCountriesConfiguration()
        service.getJobsConfiguration()
        service.getLanguagesConfiguration()
        service.getPrimaryTranslationsConfiguration()
        service.getTimezonesConfiguration()
    }

}