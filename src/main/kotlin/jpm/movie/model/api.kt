package jpm.movie.model

/**
 * Interface that represents all API messages of the service.
 */
sealed interface MovieRequest

data class RawRequest(
    val years: Set<String>,
    val names: Set<String>,
    val casts: Set<String>,
    val genres: Set<String>,
) : MovieRequest

data class ValidatedRequest(
    val years: Set<Year>,
    val names: Set<MovieName>,
    val casts: Set<CastMember>,
    val genres: Set<Genre>,
) : MovieRequest

/**
 * Interface that represents all possible answers could be sent back to the client. The interface either can
 * be a [ProcessSuccess] or a [ProcessFailure].
 *
 * Naturally, the service returns with a [ProcessFailure] in case of any kind of errors,
 * disregard the request is rejected or there was a transient error. The detailed description is in the
 * [ErrorDescription].
 *
 * In any other cases, the response is [ProcessSuccess]. The [ProcessSuccess] response may wary based on the request.
 */
sealed interface MovieResponse

sealed interface ProcessSuccess : MovieResponse
sealed interface ProcessFailure : MovieResponse

data class QueryResult(val movies: Set<Movie>) : ProcessSuccess

data class RequestFailure(val errors: Set<Error>) : ProcessFailure