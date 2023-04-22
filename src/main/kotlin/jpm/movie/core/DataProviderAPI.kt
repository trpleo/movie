package jpm.movie.core

sealed interface DataProviderAPI {
    suspend fun pushData(rawMovieData: String)
}