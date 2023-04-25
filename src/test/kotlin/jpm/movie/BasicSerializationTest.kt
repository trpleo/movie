package jpm.movie

import arrow.core.nel
import io.kotest.matchers.shouldBe
import jpm.movie.model.CastMember
import jpm.movie.model.Codecs
import jpm.movie.model.Genre
import jpm.movie.model.Movie
import jpm.movie.model.MovieName
import jpm.movie.model.QueryResult
import jpm.movie.model.RawRequest
import jpm.movie.model.RequestFailure
import jpm.movie.model.ValidatedRequest
import jpm.movie.model.ValidationError
import jpm.movie.model.Year
import org.junit.jupiter.api.Test

class BasicSerializationTest : Codecs {

    @Test
    fun `should serialize RawRequest`() {
        val request = RawRequest(
            years = setOf("1850", "2030", "2020"),
            names = setOf("99!"),
            casts = setOf(),
            genres = setOf("Dramá")
        )

        val result = request.toProtoJson().let { json -> json.fromProtoJson() }

        result shouldBe request
    }

    @Test
    fun `should serialize ValidatedRequest`() {
        val request = ValidatedRequest(
            setOf(Year(1850), Year(2030), Year(2020)),
            setOf(MovieName("99!")),
            setOf(CastMember("Very Talented Person")),
            setOf(Genre.DRAMA)
        )

        val result = request.toProtoJson().let { json -> json.fromProtoJson() }

        result shouldBe request
    }

    @Test
    fun `should serialize QueryResult`() {
        val response = QueryResult(setOf(Movie(Year(1959), MovieName("Hello Dolly!"), setOf(CastMember("P G")), setOf(Genre.ACTION))))

        val result = response.toProtoJson().let { json -> json.fromProtoJson() }

        result shouldBe response
    }

    @Test
    fun `should serialize RequestFailure`() {
        val response = RequestFailure(setOf(ValidationError.OutOfBoundYear("test-test", Year(1100).nel())))

        val result = response.toProtoJson().let { json -> json.fromProtoJson() }

        result shouldBe response
    }
}