package jpm.movie.core

import com.google.inject.Inject
import com.google.inject.Singleton
import java.util.logging.Logger
import jpm.movie.Log
import jpm.movie.core.validators.validate
import jpm.movie.model.Movie
import jpm.movie.model.MovieResponse
import jpm.movie.model.QueryResult
import jpm.movie.model.RawRequest
import jpm.movie.model.RequestFailure
import jpm.movie.model.ValidatedRequest

@Singleton
sealed class QueryServiceImpl @Inject constructor(@Log private val logger: Logger) : QueryService {

    private fun seekMovies(validatedRq: ValidatedRequest): Set<Movie> {
        return emptySet()
    }

    override suspend fun queryMovies(request: RawRequest): MovieResponse =
        request
            .validate()
            .map(::seekMovies)
            .fold(
                { RequestFailure(it.toSet()) },
                { QueryResult(it) }
            )
}