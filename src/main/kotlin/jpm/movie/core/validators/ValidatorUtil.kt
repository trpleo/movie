package jpm.movie.core.validators

import arrow.core.Either
import arrow.core.EitherNel
import arrow.core.left
import arrow.core.leftNel
import arrow.core.right
import java.util.Locale
import jpm.movie.model.CastMember
import jpm.movie.model.Genre
import jpm.movie.model.MovieName
import jpm.movie.model.ValidationError
import jpm.movie.model.Year

internal fun Set<String>.validateForYearsSearchIn(): EitherNel<ValidationError, Set<Year>> = this
    .map { s ->
        when (val i = Either.catch { s.toInt() }) {
            is Either.Right -> i
            is Either.Left -> s.left()
        }
    }
    .let { yearsList ->
        val cannotConvert = yearsList.mapNotNull { it.leftOrNull() }
        if (cannotConvert.isNotEmpty()) {
            ValidationError.InvalidInteger("Malformed number(s).", cannotConvert.nonEmptyListToNel()).leftNel()
        } else {
            validator(
                toCheck = yearsList,
                invalidWhen = {
                    yearsList.map { it.getOrNull()!! }.any { it > java.time.Year.now().value || it < 1880 }
                },
                transformer = { ys -> ys.map { Year(it.getOrNull()!!) }.toSet() },
                cause = {
                    ValidationError.OutOfBoundYear(
                        "Year validation error.",
                        yearsList
                            .map { it.getOrNull()!! }
                            .filter { it > java.time.Year.now().value || it < 1880 }
                            .map { Year(it) }
                            .nonEmptyListToNel()
                    )
                }
            )
        }
    }

internal fun Set<String>.validateForGenres() = this
    .map { gs ->
        // Locale.getDefault() usage is error-prone -> with further specification, character set can be agreed on.
        when (val e = Either.catch { Genre.valueOf(gs.uppercase(Locale.getDefault())) }) {
            is Either.Right -> e
            is Either.Left -> gs.left()
        }
    }
    .let { maybeGenres ->
        validator(
            toCheck = this,
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

internal fun Set<String>.validateForMovieNames() = this
    .map {
        when (it.matches(Regex("^[a-zA-Z0-9'&_.!?:\\s]*$"))) {
            true -> it.right()
            false -> it.left()
        }
    }
    .let { maybeName ->
        validator(
            toCheck = this,
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

internal fun Set<String>.validateForCastMembers() = this
    .map {
        when (it.matches(Regex("""^[_.\-\s\p{L}]{3,40}$"""))) {
            true -> it.right()
            false -> it.left()
        }
    }
    .let { maybeMember ->
        validator(
            toCheck = this,
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