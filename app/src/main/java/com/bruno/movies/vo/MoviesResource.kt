package com.bruno.movies.vo

import com.bruno.movies.model.Movie

sealed class MoviesResource {
    data class Items(val data: List<Movie>): MoviesResource()
    data class Error(val message: String, val err: Throwable): MoviesResource()
}