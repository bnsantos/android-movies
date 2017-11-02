package com.bruno.movies.api

import com.bruno.movies.model.Movie
import com.google.gson.annotations.SerializedName

data class PagingResponse(
        val page: Int,
        @SerializedName("total_results")
        val totalResults: Int,
        @SerializedName("total_pages")
        val totalPages: Int,
        val results: List<Movie>
)