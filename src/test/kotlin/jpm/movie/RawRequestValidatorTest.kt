package jpm.movie

import arrow.core.Either
import arrow.core.Either.Companion.zipOrAccumulate
import arrow.core.flatMap
import arrow.core.invalid
import arrow.core.invalidNel
import arrow.core.left
import arrow.core.leftNel
import arrow.core.nel
import arrow.core.raise.either
import arrow.core.right
import arrow.core.toNonEmptyListOrNull
import arrow.core.valid
import arrow.core.zip
import io.kotest.core.Tuple2
import io.kotest.matchers.shouldBe
import jpm.movie.core.validators.validate
import jpm.movie.model.CastMember
import jpm.movie.model.Genre
import jpm.movie.model.MovieName
import jpm.movie.model.RawRequest
import jpm.movie.model.ValidatedRequest
import jpm.movie.model.ValidationError
import jpm.movie.model.Year
import org.checkerframework.checker.units.qual.s
import org.junit.jupiter.api.Test

class RawRequestValidatorTest {
    @Test
    fun `should reject invalid years`() {
        val request = RawRequest(
            years = setOf("2004", "1880", "1700", "2030"),
            names = setOf(),
            casts = setOf(),
            genres = setOf()
        )

        val validated = request.validate()

        validated shouldBe ValidationError.OutOfBoundYear("Year validation error.", Year(1700).nel().plus(Year(2030)))
            .leftNel()
    }

    @Test
    fun `should validate years`() {
        val request = RawRequest(
            years = setOf("2004", "1880"),
            names = setOf(),
            casts = setOf(),
            genres = setOf()
        )

        val validated = request.validate()

        validated shouldBe ValidatedRequest(setOf(Year(2004), Year(1880)), emptySet(), emptySet(), emptySet()).right()
    }

    @Test
    fun `should reject invalid cast member names`() {
        val request = RawRequest(
            years = setOf(),
            names = setOf(),
            casts = setOf("Audrey Hepburn", "George P7eppard", "Patricia Neal"),
            genres = setOf()
        )

        val validated = request.validate()

        validated shouldBe ValidationError.InvalidCastMember(
            "Cast name is too long, or contains not allowed characters.",
            "George P7eppard".nel()
        ).leftNel()
    }

    @Test
    fun `should validate cast members`() {
        val request = RawRequest(
            years = setOf(),
            names = setOf(),
            casts = setOf("Audrey Hepburn", "George Peppard"),
            genres = setOf()
        )

        val validated = request.validate()

        validated shouldBe ValidatedRequest(
            emptySet(),
            emptySet(),
            setOf(CastMember("Audrey Hepburn"), CastMember("George Peppard")),
            emptySet()
        ).right()
    }

    @Test
    fun `should reject invalid movie names`() {
        val request = RawRequest(
            years = setOf(),
            names = setOf("Breakfast at Tiffany's", "Fast & Furious 7%"),
            casts = setOf(),
            genres = setOf()
        )

        val validated = request.validate()

        validated shouldBe ValidationError.InvalidMovieName("Movie name validation error.", "Fast & Furious 7%".nel())
            .leftNel()
    }

    @Test
    fun `should validate movie names`() {
        val request = RawRequest(
            years = setOf(),
            names = setOf("Breakfast at Tiffany's", "Fast & Furious 7"),
            casts = setOf(),
            genres = setOf()
        )

        val validated = request.validate()

        validated shouldBe ValidatedRequest(
            emptySet(),
            setOf(MovieName("Breakfast at Tiffany's"), MovieName("Fast & Furious 7")),
            emptySet(),
            emptySet()
        ).right()
    }

    @Test
    fun `should reject invalid genres`() {
        val request = RawRequest(
            years = setOf(),
            names = setOf(),
            casts = setOf(),
            genres = setOf("Dramaa", "Erótic", "Famíly", "Fantasy")
        )

        val validated = request.validate()

        val errorList = "Dramaa".nel().plus("Erótic").plus("Famíly")
        validated shouldBe ValidationError.InvalidGenre("Genre validation error.", errorList).leftNel()
    }

    @Test
    fun `should validate genres`() {
        val request = RawRequest(
            years = setOf(),
            names = setOf(),
            casts = setOf(),
            genres = setOf("Drama", "Erotic", "Family", "Fantasy")
        )

        val validated = request.validate()

        validated shouldBe ValidatedRequest(
            emptySet(),
            emptySet(),
            emptySet(),
            setOf(Genre.DRAMA, Genre.EROTIC, Genre.FAMILY, Genre.FANTASY)
        ).right()
    }

    @Test
    fun `should return complex error`() {
        val request = RawRequest(
            years = setOf("1850", "2030", "2020"),
            names = setOf("99!"),
            casts = setOf(),
            genres = setOf("Dramá")
        )

        val validated = request.validate()

        validated shouldBe ValidationError.OutOfBoundYear(
            "Year validation error.",
            setOf(Year(1850), Year(2030)).toNonEmptyListOrNull()!!
        ).nel().plus(
            ValidationError.InvalidGenre("Genre validation error.", "Dramá".nel())
        ).left()
    }
}