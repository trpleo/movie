package jpm.movie.core

import arrow.core.EitherNel
import arrow.core.flatMap
import com.google.inject.Inject
import com.google.inject.Singleton
import java.util.logging.Logger
import jpm.movie.Log
import jpm.movie.core.validators.validate
import jpm.movie.model.Movie
import jpm.movie.model.MovieResponse
import jpm.movie.model.MovieSvcError
import jpm.movie.model.QueryResult
import jpm.movie.model.RawRequest
import jpm.movie.model.RequestFailure
import jpm.movie.model.ValidatedRequest

@Singleton
class QueryServiceImpl @Inject constructor(
    private val dbBridge: DBBridge,
    @Log private val logger: Logger
) : QueryService {

    init {
        logger.info { "Query Service is up..." }
    }

    private fun seekMovies(validatedRq: ValidatedRequest): EitherNel<MovieSvcError, Set<Movie>> =
        dbBridge.seekMovie(validatedRq)

    override suspend fun queryMovies(request: RawRequest): MovieResponse {
        logger.info { "Seek for movies with query [$request]." }
        return request
            .validate()
            .flatMap(::seekMovies)
            .fold(
                { RequestFailure(it.toSet()) },
                { QueryResult(it) }
            )
    }
}