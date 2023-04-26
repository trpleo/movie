package jpm.movie.core.validators

import arrow.core.Either.Companion.zipOrAccumulate
import arrow.core.EitherNel
import arrow.core.toNonEmptyListOrNull
import jpm.movie.model.MovieSvcError
import jpm.movie.model.RawRequest
import jpm.movie.model.ValidatedRequest

fun <T> List<T>.nonEmptyListToNel() = this.toNonEmptyListOrNull()!!

private fun RawRequest.validateYearsSearchIn() = years.validateForYearsSearchIn()

private fun RawRequest.validateGenres() = genres.validateForGenres()

private fun RawRequest.validateMovieNames() = names.validateForMovieNames()

private fun RawRequest.validateCastMembers() = casts.validateForCastMembers()

fun RawRequest.validate(): EitherNel<MovieSvcError, ValidatedRequest> =
    zipOrAccumulate(
        validateYearsSearchIn(),
        validateMovieNames(),
        validateCastMembers(),
        validateGenres()
    ) { y, m, c, g -> ValidatedRequest(y, m, c, g) }

