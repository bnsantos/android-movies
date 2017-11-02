package com.bruno.movies.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity
data class Movie(
        @PrimaryKey
        val id: String,
        @SerializedName("imdb_id")
        val imdbId: String,
        val title: String,
        @SerializedName("original_title")
        val originalTitle: String,
        @SerializedName("original_language")
        val originalLanguage: String,
        @SerializedName("backdrop_path")
        val backDrop: String,
        val status: String,
        @SerializedName("release_date")
        val releaseDate: Date,
        val overview: String,
        val popularity: Float,
        @SerializedName("poster_path")
        val poster: String
)