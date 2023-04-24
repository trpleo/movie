package jpm.movie.core.validators

import arrow.core.Either
import arrow.core.EitherNel
import arrow.core.left
import arrow.core.leftNel
import arrow.core.raise.either
import arrow.core.right
import arrow.core.toNonEmptyListOrNull
import java.util.Locale
import jpm.movie.model.CastMember
import jpm.movie.model.Genre
import jpm.movie.model.MovieName
import jpm.movie.model.RawRequest
import jpm.movie.model.ValidatedRequest
import jpm.movie.model.ValidationError
import jpm.movie.model.Year
import java.time.Year as JYear
import arrow.core.Either.Companion.zipOrAccumulate

fun <T> List<T>.nonEmptyListToNel() = this.toNonEmptyListOrNull()!!

private fun RawRequest.validateYearsSearchIn(): EitherNel<ValidationError, Set<Year>> = years
    .map { s ->
        when (val i = Either.catch { s.toInt() }) {
            is Either.Right -> i
            is Either.Left -> s.left()
        }
    } // mapOrAccumulate
    .let { yearsList ->
        val cannotConvert = yearsList.mapNotNull { it.leftOrNull() }
        if (cannotConvert.isNotEmpty()) {
            ValidationError.InvalidInteger("Malformed number(s).", cannotConvert.nonEmptyListToNel()).leftNel()
        } else {
            validator(
                toCheck = yearsList,
                invalidWhen = {
                    yearsList.map { it.getOrNull()!! }.any { it > JYear.now().value || it < 1880 }
                },
                transformer = { ys -> ys.map { Year(it.getOrNull()!!) }.toSet() },
                cause = {
                    ValidationError.OutOfBoundYear(
                        "Year validation error.",
                        yearsList
                            .map { it.getOrNull()!! }
                            .filter { it > JYear.now().value || it < 1880 }
                            .map { Year(it) }
                            .nonEmptyListToNel()
                    )
                }
            )
        }
    }

private fun RawRequest.validateGenres() = genres
    .map { gs ->
        // Locale.getDefault() usage is error-prone -> with further specification, character set can be agreed on.
        when (val e = Either.catch { Genre.valueOf(gs.uppercase(Locale.getDefault())) }) {
            is Either.Right -> e
            is Either.Left -> gs.left()
        }
    }
    .let { maybeGenres ->
        validator(
            toCheck = genres,
            invalidWhen = { maybeGenres.any { it.isLeft() } },
            transformer = { maybeGenres.map { it.getOrNull()!! }.toSet() },
            cause = {
                ValidationError.InvalidGenre(
                    "Genre validation error.",
                    maybeGenres.mapNotNull { it.leftOrNull() }.nonEmptyListToNel()
                )
            }
        )
    }

private fun RawRequest.validateMovieNames() = names
    .map {
        when (it.matches(Regex("^[a-zA-Z0-9'&_.!?\\s]*$"))) {
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
                    maybeName.mapNotNull { it.leftOrNull() }.nonEmptyListToNel()
                )
            }
        )
    }

private fun RawRequest.validateCastMembers() = casts
    .map {
        when (it.matches(Regex("^[a-zA-Z_\\s]{3,40}$"))) {
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
                    "Cast name is too long, or contains not allowed characters.",
                    maybeMember.mapNotNull { it.leftOrNull() }.nonEmptyListToNel()
                )
            }
        )
    }

fun RawRequest.validate(): EitherNel<ValidationError, ValidatedRequest> =
    zipOrAccumulate(
        validateYearsSearchIn(),
        validateMovieNames(),
        validateCastMembers(),
        validateGenres()
    ) { y, m, c, g -> ValidatedRequest(y, m, c, g) }

