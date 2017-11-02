package com.bruno.movies.repository

import android.util.Log
import com.bruno.movies.api.MoviesService
import com.bruno.movies.db.MovieDao
import com.bruno.movies.defaultReleaseDate
import com.bruno.movies.model.Movie
import io.reactivex.Completable
import io.reactivex.Flowable

class MoviesRepository(
        private val key: String,
        private val dao: MovieDao,
        private val api: MoviesService) {
    private val limit = 10

    fun read(page: Int): Flowable<List<Movie>> {
        readService(page).subscribe{
            Log.i(this@MoviesRepository.javaClass.simpleName, "Successfully read discover from cloud and saved")
        }
        return readCached(page)
    }

    private fun readCached(page: Int): Flowable<List<Movie>> {
        return dao.read(limit, page * limit)
    }

    private fun cache(movies: List<Movie>) = dao.insertAll(movies)

    private fun cache(movie: Movie) = dao.insert(movie)

    private fun readService(page: Int, releaseDate: String = defaultReleaseDate()): Completable {
        return api.discover(key, page, releaseDate)
                .doOnNext {
                    cache(it.results)
                }.flatMapCompletable {
            Completable.complete()
        }
    }

    fun read(id: String): Flowable<Movie> {
        return readCached(id)
    }

    private fun readCached(id: String): Flowable<Movie> {
        readService(id).subscribe{
            Log.i(this@MoviesRepository.javaClass.simpleName, "Successfully read details from cloud and saved")
        }
        return dao.read(id)
    }

    private fun readService(id: String): Completable {
        return api.details(id, key)
                .doOnNext{
                    cache(it)
                }.flatMapCompletable {
            Completable.complete()
        }
    }
}