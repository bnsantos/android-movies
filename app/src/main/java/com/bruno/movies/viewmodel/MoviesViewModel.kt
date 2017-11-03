package com.bruno.movies.viewmodel

import android.util.Log
import com.bruno.movies.repository.MoviesRepository
import com.bruno.movies.vo.MoviesResource
import io.reactivex.Observable

class MoviesViewModel(private val repository: MoviesRepository) {

    fun read(page: Int): Observable<MoviesResource> {
        return repository.read(page)
                .map {
                    MoviesResource.Items(it) as MoviesResource
                }
                .onErrorReturn {
                    Log.i(MoviesViewModel::class.java.simpleName, "Error while reading movies", it)
                    MoviesResource.Error("", it)
                }
                .toObservable() // Converting to Observable is it a good thing?
    }
}