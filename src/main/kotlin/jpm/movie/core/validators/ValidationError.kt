package jpm.movie.core.validators

import arrow.core.Nel
import jpm.movie.model.Year

sealed interface ValidationError {

    val cause: String

    data class OutOfBoundYear(override val cause: String, val invalidYears: Nel<Year>) : ValidationError

    data class InvalidGenre(override val cause: String, val invalidGenres: Nel<String>) : ValidationError

    data class InvalidMovieName(override val cause: String, val invalidNames: Nel<String>) : ValidationError

    data class InvalidCastMember(override val cause: String, val invalidNames: Nel<String>) : ValidationError
}