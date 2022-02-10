package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.PopularTvResponse
import org.koin.core.component.KoinComponent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TvService: KoinComponent {

    private val service by lazy { TmdbClient().createTvService() }

    fun getPopularTv(page: Int = 1, callback: (isSuccessful: Boolean, response: PopularTvResponse?) -> Unit) {
        service.getPoplarTv(page = page).enqueue(object : Callback<PopularTvResponse> {
            override fun onResponse(call: Call<PopularTvResponse>, response: Response<PopularTvResponse>) {
                response.body()?.let { body ->
                    callback.invoke(true, body)
                } ?: run {
                    callback.invoke(false, null)
                }
            }

            override fun onFailure(call: Call<PopularTvResponse>, t: Throwable) {
                callback.invoke(false, null)
            }
        })
    }
}