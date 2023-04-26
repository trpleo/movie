package jpm.movie.core

import arrow.core.EitherNel
import arrow.core.leftNel
import com.google.inject.Inject
import com.google.inject.Singleton
import java.util.logging.Logger
import jpm.movie.Log
import jpm.movie.model.Movie
import jpm.movie.model.MovieSvcError
import jpm.movie.model.ValidatedRequest
import jpm.movie.model.ValidationError


@Singleton
class DBBridgeImpl @Inject constructor(
    private val config: DbBridgeConfig,
    @Log private val logger: Logger,
) : DBBridge {

    init {
        logger.info { "DB Bridge is starting, with configuration [$config]..." }
    }

    override fun persistMovie(movie: Movie): EitherNel<MovieSvcError, Movie> {
        // todo: jooq persistence
        return ValidationError.DataProviderError("Not yet implemented").leftNel()
    }

    override fun seekMovie(query: ValidatedRequest): EitherNel<MovieSvcError, Set<Movie>> {
        // todo: jooq query
        return ValidationError.DataProviderError("Not yet implemented").leftNel()
    }
}