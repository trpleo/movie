package jpm.movie

import arrow.core.Either.Companion.zipOrAccumulate
import arrow.core.EitherNel
import arrow.core.nel
import arrow.core.right
import com.google.protobuf.Struct
import com.google.protobuf.util.JsonFormat
import io.kotest.matchers.shouldBe
import jpm.movie.core.QueueBridgeImpl.Companion.validateAndConvertMessage
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
        val response = QueryResult(
            setOf(
                Movie(
                    Year(1959),
                    MovieName("Hello Dolly!"),
                    setOf(CastMember("P G")),
                    setOf(Genre.ACTION)
                )
            )
        )

        val result = response.toProtoJson().let { json -> json.fromProtoJson() }

        result shouldBe response
    }

    @Test
    fun `should serialize RequestFailure`() {
        val response = RequestFailure(setOf(ValidationError.OutOfBoundYear("test-test", Year(1100).nel())))

        val result = response.toProtoJson().let { json -> json.fromProtoJson() }

        result shouldBe response
    }

    @Test
    fun `should find properties in Data Provider's message`() {
        val msg = """{
            title: "Avengers: Age of Ultron",
            year: 2015,
            cast: [
              "Robert Downey Jr.",
              "Chris Hemsworth",
              "Mark Ruffalo",
              "Chris Evans",
              "Scarlett Johansson",
              "Jeremy Renner",
              "Don Cheadle",
              "Aaron Taylor-Johnson",
              "Elizabeth Olsen",
              "Paul Bettany",
              "Cobie Smulders",
              "Anthony Mackie",
              "Hayley Atwell",
              "Idris Elba",
              "Stellan Skarsgård",
              "James Spader",
              "Samuel L. Jackson",
            ],
            genres: ["Superhero"],
            href: "Avengers:_Age_of_Ultron",
            extract:
              "Avengers: Age of Ultron is a 2015 American superhero film based on the Marvel Comics superhero team the Avengers. Produced by Marvel Studios and distributed by Walt Disney Studios Motion Pictures, it is the sequel to The Avengers (2012) and the 11th film in the Marvel Cinematic Universe (MCU). Written and directed by Joss Whedon, the film features an ensemble cast including Robert Downey Jr., Chris Hemsworth, Mark Ruffalo, Chris Evans, Scarlett Johansson, Jeremy Renner, Don Cheadle, Aaron Taylor-Johnson, Elizabeth Olsen, Paul Bettany, Cobie Smulders, Anthony Mackie, Hayley Atwell, Idris Elba, Linda Cardellini, Stellan Skarsgård, James Spader, and Samuel L. Jackson. In the film, the Avengers fight Ultron (Spader)—an artificial intelligence created by Tony Stark (Downey) and Bruce Banner (Ruffalo) who plans to bring about world peace by causing human extinction.",
            thumbnail:
              "https://upload.wikimedia.org/wikipedia/en/f/ff/Avengers_Age_of_Ultron_poster.jpg"
          }"""

        validateAndConvertMessage(msg) shouldBe Movie(
            Year(2015),
            MovieName("Avengers: Age of Ultron"),
            setOf(
                CastMember("Robert Downey Jr."),
                CastMember("Chris Hemsworth"),
                CastMember("Mark Ruffalo"),
                CastMember("Chris Evans"),
                CastMember("Scarlett Johansson"),
                CastMember("Jeremy Renner"),
                CastMember("Don Cheadle"),
                CastMember("Aaron Taylor-Johnson"),
                CastMember("Elizabeth Olsen"),
                CastMember("Paul Bettany"),
                CastMember("Cobie Smulders"),
                CastMember("Anthony Mackie"),
                CastMember("Hayley Atwell"),
                CastMember("Idris Elba"),
                CastMember("Stellan Skarsgård"),
                CastMember("James Spader"),
                CastMember("Samuel L. Jackson"),
            ),
            setOf(Genre.SUPERHERO)
        ).right()
    }
}