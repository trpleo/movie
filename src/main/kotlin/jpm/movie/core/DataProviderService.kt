package jpm.movie.core

sealed class DataProviderService() : DataProviderAPI {

    override suspend fun pushData(rawMovieData: String) {
        TODO("Not yet implemented")
    }
}