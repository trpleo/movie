package jpm.movie.core.validators

import arrow.core.Either
import arrow.core.EitherNel
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import arrow.core.toNonEmptyListOrNull
import java.time.Year
import jpm.movie.model.CastMember
import jpm.movie.model.Genre
import jpm.movie.model.MovieName
import jpm.movie.model.RawRequest
import jpm.movie.model.ValidatedRequest
import jpm.movie.model.Year as YS

fun <T> List<T>.nonEmptyListToNel() = this.toNonEmptyListOrNull()!!

private fun RawRequest.validateYearsSearchIn() = years
    .filter { it > Year.now().value || it < 1880 }
    .let { wrongYears ->
        validator(
            toCheck = years,
            invalidWhen = { wrongYears.isNotEmpty() },
            transformer = { ys -> ys.map { YS(it) }.toSet() },
            cause = {
                ValidationError.OutOfBoundYear("Year validation error.", wrongYears.map { YS(it) }.nonEmptyListToNel())
            }
        )
    }

private fun RawRequest.validateGenres() = genres
    .map { gs ->
        when (val e = Either.catch { Genre.valueOf(gs) }) {
            is Either.Right -> e
            is Either.Left -> gs.left()
        }
    }.let { maybeGenres ->
        validator(
            toCheck = genres,
            invalidWhen = { maybeGenres.any { it.isLeft() } },
            transformer = { maybeGenres.map { it.getOrNull()!! }.toSet() },
            cause = {
                ValidationError.InvalidGenre(
                    "Genre validation error.",
                    maybeGenres.map { it.leftOrNull() }.filterNotNull().nonEmptyListToNel()
                )
            }
        )
    }

private fun RawRequest.validateMovieNames() = names
    .map {
        when (it.matches(Regex("^[a-zA-Z0-9\\s]*$"))) {
            true -> it.right()
            false -> it.left()
        }
    }
    .let { maybeName ->
        validator(
            toCheck = names,
            invalidWhen = { maybeName.any { it.isLeft() } },
            transformer = { maybeName.map { MovieName(it.getOrNull()!!) }.toSet() },
            cause = {
                ValidationError.InvalidMovieName(
                    "Movie name validation error.",
                    maybeName.map { it.leftOrNull() }.filterNotNull().nonEmptyListToNel()
                )
            }
        )
    }

private fun RawRequest.validateCastMembers() = casts
    .map {
        when (it.matches(Regex("^[A-Za-z][A-Za-z0-9_]{7,29}$"))) {
            true -> it.right()
            false -> it.left()
        }
    }
    .let { maybeMember ->
        validator(
            toCheck = casts,
            invalidWhen = { maybeMember.any { it.isLeft() } },
            transformer = { maybeMember.map { CastMember(it.getOrNull()!!) }.toSet() },
            cause = {
                ValidationError.InvalidCastMember(
                    "",
                    maybeMember.map { it.leftOrNull() }.filterNotNull().nonEmptyListToNel()
                )
            }
        )
    }

fun RawRequest.validate(): EitherNel<ValidationError, ValidatedRequest> =
    either {
        ValidatedRequest(
            validateYearsSearchIn().bind(),
            validateMovieNames().bind(),
            validateCastMembers().bind(),
            validateGenres().bind()
        )
    }
