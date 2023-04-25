package jpm.movie.core.validators

import arrow.core.Either.Companion.zipOrAccumulate
import arrow.core.EitherNel
import jpm.movie.model.Movie
import jpm.movie.model.ValidationError

data class DataProviderMessage(
    val year: String,
    val title: String,
    val cast: List<String>,
    val genre: List<String>,
)

private fun DataProviderMessage.validateYearsSearchIn() = setOf(year).validateForYearsSearchIn()
private fun DataProviderMessage.validateGenres() = genre.toSet().validateForGenres()
private fun DataProviderMessage.validateMovieNames() = setOf(title).validateForMovieNames()
private fun DataProviderMessage.validateCastMembers() = cast.toSet().validateForCastMembers()

private fun <T> Set<T>.safeFirst(default: () -> T) = if (this.isEmpty()) default() else this.first()

fun DataProviderMessage.validate(): EitherNel<ValidationError, Movie> =
    zipOrAccumulate(
        validateYearsSearchIn(),
        validateMovieNames(),
        validateCastMembers(),
        validateGenres()
    ) { y, m, c, g -> Movie(y.safeFirst { null }, m.safeFirst { null }, c, g) }