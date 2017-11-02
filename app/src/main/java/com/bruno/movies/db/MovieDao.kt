package com.bruno.movies.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.bruno.movies.model.Movie
import io.reactivex.Flowable

@Dao
interface MovieDao {
    @Insert
    fun insertAll(movies: List<Movie>)

    @Insert
    fun insert(movie: Movie)

    @Query("SELECT * FROM movie ORDER BY defaultReleaseDate DESC LIMIT :limit OFFSET :offset")
    fun read(limit: Int, offset: Int): Flowable<List<Movie>>

    @Query("SELECT * FROM movie WHERE id = :id")
    fun read(id: String): Flowable<Movie>
}