package jpm.movie.model

import arrow.core.Nel

sealed interface Error

/**
 * type alias only for convenience: not to confuse with [kotlin.Error]
 */
typealias MovieSvcError = Error

sealed interface ValidationError : Error {

    val cause: String

    data class OutOfBoundYear(override val cause: String, val invalidYears: Nel<Year>) : ValidationError

    data class InvalidInteger(override val cause: String, val invalidYears: Nel<String>) : ValidationError

    data class InvalidGenre(override val cause: String, val invalidGenres: Nel<String>) : ValidationError

    data class InvalidMovieName(override val cause: String, val invalidNames: Nel<String>) : ValidationError

    data class InvalidCastMember(override val cause: String, val invalidNames: Nel<String>) : ValidationError

    data class DataProviderError(override val cause: String) : ValidationError
}

data class GeneralError(val cause: String) : Error