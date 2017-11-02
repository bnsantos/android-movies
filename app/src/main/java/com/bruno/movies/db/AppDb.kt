package com.bruno.movies.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.bruno.movies.model.Movie

@Database(entities = arrayOf(Movie::class), version = 1)
abstract class AppDb: RoomDatabase(){
    abstract fun movieDao(): MovieDao
}