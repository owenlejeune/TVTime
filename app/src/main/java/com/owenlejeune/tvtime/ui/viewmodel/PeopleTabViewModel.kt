package com.owenlejeune.tvtime.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.HomePagePeoplePagingSource
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.HomePagePerson
import kotlinx.coroutines.flow.Flow

class PeopleTabViewModel: ViewModel() {

    val popularPeople: Flow<PagingData<HomePagePerson>> = Pager(PagingConfig(pageSize = ViewModelConstants.PAGING_SIZE)) {
        HomePagePeoplePagingSource()
    }.flow.cachedIn(viewModelScope)

}