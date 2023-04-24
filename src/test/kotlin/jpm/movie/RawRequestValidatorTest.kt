package jpm.movie

import arrow.core.leftNel
import arrow.core.nel
import arrow.core.right
import io.kotest.matchers.shouldBe
import jpm.movie.core.validators.validate
import jpm.movie.model.CastMember
import jpm.movie.model.Genre
import jpm.movie.model.MovieName
import jpm.movie.model.RawRequest
import jpm.movie.model.ValidatedRequest
import jpm.movie.model.ValidationError
import jpm.movie.model.Year
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
}