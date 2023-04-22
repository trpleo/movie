package jpm.movie.core

import arrow.core.Either
import jpm.movie.core.validators.validate
import jpm.movie.model.ProcessFailure
import jpm.movie.model.ProcessSuccess
import jpm.movie.model.RawRequest

sealed class QueryService() : QueryAPI {

    override suspend fun queryMovies(request: RawRequest): Either<ProcessFailure, ProcessSuccess> {

        val validatedQuery = request.validate()

        validatedQuery.also(::println)

        // check the query against the movie data set

        TODO("Not yet implemented")
    }
}