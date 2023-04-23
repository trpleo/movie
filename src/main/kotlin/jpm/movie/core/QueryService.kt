package jpm.movie.core

import jpm.movie.core.validators.validate
import jpm.movie.model.Movie
import jpm.movie.model.MovieResponse
import jpm.movie.model.QueryResult
import jpm.movie.model.RawRequest
import jpm.movie.model.RequestFailure
import jpm.movie.model.ValidatedRequest

sealed class QueryService() : QueryAPI {

    private fun seekMovies(validatedRq: ValidatedRequest): Set<Movie> {
        TODO()
    }

    override suspend fun queryMovies(request: RawRequest): MovieResponse {
        return request
            .validate()
            .map(::seekMovies)
            .fold(
                { RequestFailure(it.toSet()) },
                { QueryResult(it) }
            )
    }
}