package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.PopularMoviesResponse
import org.koin.core.component.KoinComponent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MoviesService: KoinComponent {

    private val service by lazy { TmdbClient().createMovieService() }

    fun getPopularMovies(page: Int = 1, callback: (isSuccessful: Boolean, response: PopularMoviesResponse?) -> Unit) {
        service.getPopularMovies(page = page).enqueue(object : Callback<PopularMoviesResponse> {
            override fun onResponse(
                call: Call<PopularMoviesResponse>,
                response: Response<PopularMoviesResponse>
            ) {
                response.body()?.let { body ->
                    callback.invoke(true, body)
                } ?: run {
                    callback.invoke(false, null)
                }
            }

            override fun onFailure(call: Call<PopularMoviesResponse>, t: Throwable) {
                callback.invoke(false, null)
            }
        })
    }

}