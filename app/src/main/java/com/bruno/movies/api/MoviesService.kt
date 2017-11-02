package com.bruno.movies.api

import com.bruno.movies.model.Movie
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*

interface MoviesService {
    @GET("3/discover/movie")
    fun discover(@Query("api_key") key: String,
                   @Query("page") page: Int,
                   @Query("primary_release_date.lte") releaseLte: String,
                   @Query("language") language: String = Locale.getDefault().toLanguageTag(),
                   @Query("sort_by") sort: String = "primary_release_date.desc"): Observable<PagingResponse>

    @GET("3/movie/{id}")
    fun details(@Path("id") id: String,
                @Query("api_key") key: String,
                @Query("language") language: String = Locale.getDefault().toLanguageTag()): Observable<Movie>
}