package com.owenlejeune.tvtime.api.tmdb.api.v3

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import com.owenlejeune.tvtime.api.tmdb.TmdbClient
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailCast
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailCrew
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailPerson
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailedMovie
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ExternalIds
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.HomePagePeopleResponse
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.PersonCreditsResponse
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.PersonImage
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.PersonImageCollection
import okhttp3.internal.notify
import org.koin.core.component.KoinComponent
import retrofit2.Response
import java.util.Collections

class PeopleService: KoinComponent {

    private val TAG = "PeopleService"

    private val service by lazy { TmdbClient().createPeopleService() }

    val peopleMap = Collections.synchronizedMap(mutableStateMapOf<Int, DetailPerson>())
    val castMap = Collections.synchronizedMap(mutableStateMapOf<Int, List<DetailCast>>())
    val crewMap = Collections.synchronizedMap(mutableStateMapOf<Int, List<DetailCrew>>())
    val imagesMap = Collections.synchronizedMap(mutableStateMapOf<Int, List<PersonImage>>())
    val externalIdsMap = Collections.synchronizedMap(mutableStateMapOf<Int, ExternalIds>())

    suspend fun getPerson(id: Int) {
        val response = service.getPerson(id)
        if (response.isSuccessful) {
            response.body()?.let {
                Log.d(TAG, "Successfully got person $id")
                peopleMap[id] = it
            } ?: run {
                Log.w(TAG, "Problem getting person $id")
            }
        } else {
            Log.e(TAG, "Issue getting person $id")
        }
    }

    suspend fun getCredits(id: Int) {
        val response = service.getCredits(id)
        if (response.isSuccessful) {
            response.body()?.let {
                Log.d(TAG, "Successfully got credits $id")
                castMap[id] = it.cast
                crewMap[id] = it.crew
            } ?: run {
                Log.w(TAG, "Problem getting credits $id")
            }
        } else {
            Log.e(TAG, "Issue getting credits $id")
        }
    }

    suspend fun getImages(id: Int) {
        val response = service.getImages(id)
        if (response.isSuccessful) {
            response.body()?.let {
                Log.d(TAG, "Successfully got images $id")
                imagesMap[id] = it.images
            } ?: run {
                Log.w(TAG, "Problem getting images $id")
            }
        } else {
            Log.e(TAG, "Issues getting images $id")
        }
    }

    suspend fun getExternalIds(id: Int) {
        val response = service.getExternalIds(id)
        if (response.isSuccessful) {
            response.body()?.let {
                Log.d(TAG, "Successfully got external ids $id")
                externalIdsMap[id] = it
            } ?: run {
                Log.w(TAG, "Problem getting external ids $id")
            }
        } else {
            Log.e(TAG, "Issue getting external ids $id")
        }
    }

    suspend fun getPopular(page: Int): Response<HomePagePeopleResponse> {
        return service.getPopular(page)
    }

}