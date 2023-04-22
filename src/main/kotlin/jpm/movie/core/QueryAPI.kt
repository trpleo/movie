package jpm.movie.core

import arrow.core.Either
import jpm.movie.model.ProcessFailure
import jpm.movie.model.ProcessSuccess
import jpm.movie.model.RawRequest

sealed interface QueryAPI {
    /**
     * The query movies API is about to request the movie stored in the service. The rules for the parameters are
     * the following:
     * - each property can contain a set of parameters, that are combined with OR,
     * - if more than one parameter contains values, those parameters will be combined with AND,
     * - when a property does not contains any parameter, the given parameter does not used for filtering.
     *
     * If a movie's searched parameter is null, that won't be represented in the result set.
     *
     * Example:
     * - (2004 OR 2005) AND THRILLER
     */
    suspend fun queryMovies(request: RawRequest): Either<ProcessFailure, ProcessSuccess>
}