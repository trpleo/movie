package jpm.movie.model

import arrow.core.NonEmptyList
import arrow.core.toNonEmptyListOrNull
import com.google.protobuf.util.JsonFormat
import jpm.movie.model.proto.ApiMessageProto
import jpm.movie.model.proto.ApiMessageProto.MovieRequestProto
import jpm.movie.model.proto.ApiMessageProto.MovieRequestProto.ValidatedRequestProto
import jpm.movie.model.proto.CastMemberProto
import jpm.movie.model.proto.ErrorProto
import jpm.movie.model.proto.ErrorProto.GeneralErrorProto
import jpm.movie.model.proto.ErrorProto.ValidationErrorProto
import jpm.movie.model.proto.ErrorProto.ValidationErrorProto.InvalidCastMemberProto
import jpm.movie.model.proto.ErrorProto.ValidationErrorProto.InvalidGenreProto
import jpm.movie.model.proto.ErrorProto.ValidationErrorProto.InvalidIntegerProto
import jpm.movie.model.proto.ErrorProto.ValidationErrorProto.InvalidMovieNameProto
import jpm.movie.model.proto.ErrorProto.ValidationErrorProto.OutOfBoundYearProto
import jpm.movie.model.proto.GenreProto
import jpm.movie.model.proto.MovieNameProto
import jpm.movie.model.proto.MovieProto
import jpm.movie.model.proto.MovieResponseProto
import jpm.movie.model.proto.YearProto
import com.google.protobuf.Message as ProtoMessage


interface Codecs {

    fun ApiMessage.toProto(): ProtoMessage = ApiMessageProto.newBuilder().let { builder ->
        when (this) {
            is MovieRequest -> builder.movieRequest = toProto()
            is MovieResponse -> builder.movieResponse = toProto()
        }
        builder
    }.build()

    fun ApiMessageProto.fromProto(): ApiMessage = when (typesCase) {
        ApiMessageProto.TypesCase.MOVIEREQUEST -> movieRequest.fromProto()
        ApiMessageProto.TypesCase.MOVIERESPONSE -> movieResponse.fromProto()
        ApiMessageProto.TypesCase.TYPES_NOT_SET, null -> throw RuntimeException("Invalid type to encode.")
    }

    /** Request Serializers */

    fun QueryResult.toProto(): MovieResponseProto.QueryResultProto =
        MovieResponseProto.QueryResultProto.newBuilder().addAllMovies(movies.map { it.toProto() }).build()

    fun MovieResponseProto.QueryResultProto.fromProto() = QueryResult(moviesList.map { it.fromProto() }.toSet())

    fun Movie.toProto(): MovieProto = MovieProto.newBuilder()
        .setYear(year?.toProto())
        .setName(name?.toProto())
        .addAllCast(cast?.map { it.toProto() })
        .addAllGenre(genre?.map { it.toProto() })
        .build()

    fun MovieProto.fromProto() = Movie(
        year.fromProto(),
        name.fromProto(),
        castList.map { it.fromProto() }.toSet(),
        genreList.map { it.fromProto() }.toSet()
    )

    fun MovieName.toProto(): MovieNameProto = MovieNameProto.newBuilder().setName(name).build()

    fun MovieNameProto.fromProto() = MovieName(name)

    fun CastMember.toProto(): CastMemberProto = CastMemberProto.newBuilder().setName(name).build()

    fun CastMemberProto.fromProto() = CastMember(name)

    fun Genre.toProto() = when (this) {
        Genre.ACTION -> GenreProto.ACTION
        Genre.ADVENTURE -> GenreProto.ADVENTURE
        Genre.ANIMATED -> GenreProto.ANIMATED
        Genre.BIOGRAPHY -> GenreProto.BIOGRAPHY
        Genre.COMEDY -> GenreProto.COMEDY
        Genre.CRIME -> GenreProto.CRIME
        Genre.DANCE -> GenreProto.DANCE
        Genre.DISASTER -> GenreProto.DISASTER
        Genre.DOCUMENTARY -> GenreProto.DOCUMENTARY
        Genre.DRAMA -> GenreProto.DRAMA
        Genre.EROTIC -> GenreProto.EROTIC
        Genre.FAMILY -> GenreProto.FAMILY
        Genre.FANTASY -> GenreProto.FANTASY
        Genre.FOUND_FOOTAGE -> GenreProto.FOUND_FOOTAGE
        Genre.HISTORICAL -> GenreProto.HISTORICAL
        Genre.HORROR -> GenreProto.HORROR
        Genre.INDEPENDENT -> GenreProto.INDEPENDENT
        Genre.LEGAL -> GenreProto.LEGAL
        Genre.LIVE_ACTION -> GenreProto.LIVE_ACTION
        Genre.MARTIAL_ARTS -> GenreProto.MARTIAL_ARTS
        Genre.MUSICAL -> GenreProto.MUSICAL
        Genre.MYSTERY -> GenreProto.MYSTERY
        Genre.NOIR -> GenreProto.NOIR
        Genre.PERFORMANCE -> GenreProto.PERFORMANCE
        Genre.POLITICAL -> GenreProto.POLITICAL
        Genre.ROMANCE -> GenreProto.ROMANCE
        Genre.SATIRE -> GenreProto.SATIRE
        Genre.SCIENCE_FICTION -> GenreProto.SCIENCE_FICTION
        Genre.SHORT -> GenreProto.SHORT
        Genre.SILENT -> GenreProto.SILENT
        Genre.SLASHER -> GenreProto.SLASHER
        Genre.SPORTS -> GenreProto.SPORTS
        Genre.SPY -> GenreProto.SPY
        Genre.SUPERHERO -> GenreProto.SUPERHERO
        Genre.SUPERNATURAL -> GenreProto.SUPERNATURAL
        Genre.SUSPENSE -> GenreProto.SUSPENSE
        Genre.TEEN -> GenreProto.TEEN
        Genre.THRILLER -> GenreProto.THRILLER
        Genre.WAR -> GenreProto.WAR
        Genre.WESTERN -> GenreProto.WESTERN
    }

    fun GenreProto.fromProto() = when (this) {
        GenreProto.ACTION -> Genre.ACTION
        GenreProto.ADVENTURE -> Genre.ADVENTURE
        GenreProto.ANIMATED -> Genre.ANIMATED
        GenreProto.BIOGRAPHY -> Genre.BIOGRAPHY
        GenreProto.COMEDY -> Genre.COMEDY
        GenreProto.CRIME -> Genre.CRIME
        GenreProto.DANCE -> Genre.DANCE
        GenreProto.DISASTER -> Genre.DISASTER
        GenreProto.DOCUMENTARY -> Genre.DOCUMENTARY
        GenreProto.DRAMA -> Genre.DRAMA
        GenreProto.EROTIC -> Genre.EROTIC
        GenreProto.FAMILY -> Genre.FAMILY
        GenreProto.FANTASY -> Genre.FANTASY
        GenreProto.FOUND_FOOTAGE -> Genre.FOUND_FOOTAGE
        GenreProto.HISTORICAL -> Genre.HISTORICAL
        GenreProto.HORROR -> Genre.HORROR
        GenreProto.INDEPENDENT -> Genre.INDEPENDENT
        GenreProto.LEGAL -> Genre.LEGAL
        GenreProto.LIVE_ACTION -> Genre.LIVE_ACTION
        GenreProto.MARTIAL_ARTS -> Genre.MARTIAL_ARTS
        GenreProto.MUSICAL -> Genre.MUSICAL
        GenreProto.MYSTERY -> Genre.MYSTERY
        GenreProto.NOIR -> Genre.NOIR
        GenreProto.PERFORMANCE -> Genre.PERFORMANCE
        GenreProto.POLITICAL -> Genre.POLITICAL
        GenreProto.ROMANCE -> Genre.ROMANCE
        GenreProto.SATIRE -> Genre.SATIRE
        GenreProto.SCIENCE_FICTION -> Genre.SCIENCE_FICTION
        GenreProto.SHORT -> Genre.SHORT
        GenreProto.SILENT -> Genre.SILENT
        GenreProto.SLASHER -> Genre.SLASHER
        GenreProto.SPORTS -> Genre.SPORTS
        GenreProto.SPY -> Genre.SPY
        GenreProto.SUPERHERO -> Genre.SUPERHERO
        GenreProto.SUPERNATURAL -> Genre.SUPERNATURAL
        GenreProto.SUSPENSE -> Genre.SUSPENSE
        GenreProto.TEEN -> Genre.TEEN
        GenreProto.THRILLER -> Genre.THRILLER
        GenreProto.WAR -> Genre.WAR
        GenreProto.WESTERN -> Genre.WESTERN
        else -> throw RuntimeException("Invalid type to encode.")
    }

    fun RawRequest.toProto(): MovieRequestProto.RawRequestProto =
        MovieRequestProto.RawRequestProto.newBuilder()
            .addAllYears(years)
            .addAllNames(names)
            .addAllCasts(casts)
            .addAllGenres(genres)
            .build()

    fun MovieRequestProto.RawRequestProto.fromProto() =
        RawRequest(yearsList.toSet(), namesList.toSet(), castsList.toSet(), genresList.toSet())

    fun ValidatedRequest.toProto(): ValidatedRequestProto = ValidatedRequestProto.newBuilder()
        .addAllYears(years.map { it.toProto() })
        .addAllNames(names.map { it.toProto() })
        .addAllCasts(casts.map { it.toProto() })
        .addAllGenres(genres.map { it.toProto() })
        .build()

    fun ValidatedRequestProto.fromProto(): ValidatedRequest = ValidatedRequest(
        years = yearsList.map { it.fromProto() }.toSet(),
        names = namesList.map { it.fromProto() }.toSet(),
        casts = castsList.map { it.fromProto() }.toSet(),
        genres = genresList.map { it.fromProto() }.toSet()
    )

    fun RequestFailure.toProto(): MovieResponseProto.RequestFailureProto =
        MovieResponseProto.RequestFailureProto.newBuilder()
            .addAllErrors(errors.map { it.toProto() })
            .build()

    fun MovieResponseProto.RequestFailureProto.fromProto() = RequestFailure(errorsList.map { it.fromProto() }.toSet())

    fun Error.toProto(): ErrorProto = ErrorProto.newBuilder().let { builder ->
        when (this) {
            is GeneralError -> builder.generalError = toProto()
            is ValidationError -> builder.validationError = toProto()
        }
        builder
    }.build()

    fun ErrorProto.fromProto() = when (typesCase) {
        ErrorProto.TypesCase.VALIDATIONERROR -> validationError.fromProto()
        ErrorProto.TypesCase.GENERALERROR -> generalError.fromProto()

        ErrorProto.TypesCase.TYPES_NOT_SET, null -> throw RuntimeException("unrecognized proto message")
    }

    fun GeneralError.toProto(): GeneralErrorProto = GeneralErrorProto.newBuilder().setCause(cause).build()

    fun GeneralErrorProto.fromProto() = GeneralError(cause)

    fun ValidationError.toProto() = ValidationErrorProto.newBuilder().let { builder ->
        when (this) {
            is ValidationError.InvalidCastMember -> builder.invalidCastMember = toProto()
            is ValidationError.InvalidGenre -> builder.invalidGenre = toProto()
            is ValidationError.InvalidInteger -> builder.invalidInteger = toProto()
            is ValidationError.InvalidMovieName -> builder.invalidMovieName = toProto()
            is ValidationError.OutOfBoundYear -> builder.outOfBoundYear = toProto()
            is ValidationError.DataProviderError -> builder.dataProviderError = toProto()
        }
        builder
    }.build()

    fun ValidationErrorProto.fromProto() = when (typesCase) {
        ValidationErrorProto.TypesCase.OUTOFBOUNDYEAR -> outOfBoundYear.fromProto()
        ValidationErrorProto.TypesCase.INVALIDINTEGER -> invalidInteger.fromProto()
        ValidationErrorProto.TypesCase.INVALIDMOVIENAME -> invalidMovieName.fromProto()
        ValidationErrorProto.TypesCase.INVALIDCASTMEMBER -> invalidCastMember.fromProto()
        ValidationErrorProto.TypesCase.INVALIDGENRE -> invalidGenre.fromProto()
        ValidationErrorProto.TypesCase.DATAPROVIDERERROR -> dataProviderError.fromProto()

        ValidationErrorProto.TypesCase.TYPES_NOT_SET, null -> throw RuntimeException("unrecognized proto message")
    }

    fun ValidationError.InvalidCastMember.toProto(): InvalidCastMemberProto =
        InvalidCastMemberProto.newBuilder().setCause(cause).addAllInvalidNames(invalidNames).build()

    fun InvalidCastMemberProto.fromProto() =
        ValidationError.InvalidCastMember(cause, invalidNamesList.fromProtoAsNel { it })

    fun ValidationError.InvalidGenre.toProto(): InvalidGenreProto =
        InvalidGenreProto.newBuilder().setCause(cause).addAllInvalidGenres(invalidGenres).build()

    fun InvalidGenreProto.fromProto() = ValidationError.InvalidGenre(cause, invalidGenresList.fromProtoAsNel { it })

    fun ValidationError.InvalidInteger.toProto(): InvalidIntegerProto =
        InvalidIntegerProto.newBuilder().setCause(cause).addAllInvalidYears(invalidYears).build()

    fun InvalidIntegerProto.fromProto() = ValidationError.InvalidInteger(cause, invalidYearsList.fromProtoAsNel { it })

    fun ValidationError.InvalidMovieName.toProto(): InvalidMovieNameProto =
        InvalidMovieNameProto.newBuilder().setCause(cause).addAllInvalidNames(invalidNames).build()

    fun InvalidMovieNameProto.fromProto() =
        ValidationError.InvalidMovieName(cause, invalidNamesList.fromProtoAsNel { it })

    fun ValidationError.OutOfBoundYear.toProto(): OutOfBoundYearProto =
        OutOfBoundYearProto.newBuilder().setCause(cause).addAllInvalidYears(invalidYears.map { it.year }).build()

    fun OutOfBoundYearProto.fromProto() =
        ValidationError.OutOfBoundYear(cause, invalidYearsList.fromProtoAsNel { Year(it) })

    fun ValidationError.DataProviderError.toProto(): ValidationErrorProto.DataProviderErrorProto =
        ValidationErrorProto.DataProviderErrorProto.newBuilder().setCause(cause).build()

    fun ValidationErrorProto.DataProviderErrorProto.fromProto() = ValidationError.DataProviderError(cause)

    /** Response Serializers */

    fun MovieResponse.toProto(): MovieResponseProto = MovieResponseProto.newBuilder().let { builder ->
        when (this) {
            is RequestFailure -> builder.failure = toProto()
            is QueryResult -> builder.success = toProto()
        }
        builder
    }.build()

    fun MovieResponseProto.fromProto(): MovieResponse = when (typesCase) {
        MovieResponseProto.TypesCase.SUCCESS -> success.fromProto()
        MovieResponseProto.TypesCase.FAILURE -> failure.fromProto()
        MovieResponseProto.TypesCase.TYPES_NOT_SET, null -> throw RuntimeException("unrecognized proto message")
    }

    fun MovieRequest.toProto(): MovieRequestProto = MovieRequestProto.newBuilder().let { builder ->
        when (this) {
            is RawRequest -> builder.rawRequestProto = toProto()
            is ValidatedRequest -> builder.validatedRequestProto = toProto()
        }
        builder
    }.build()

    fun MovieRequestProto.fromProto(): MovieRequest = when (typesCase) {
        MovieRequestProto.TypesCase.RAWREQUESTPROTO -> rawRequestProto.fromProto()
        MovieRequestProto.TypesCase.VALIDATEDREQUESTPROTO -> validatedRequestProto.fromProto()
        MovieRequestProto.TypesCase.TYPES_NOT_SET, null -> throw RuntimeException("unrecognized proto message")
    }


    fun Year.toProto(): YearProto = YearProto.newBuilder().setYear(year).build()

    fun YearProto.fromProto() = Year(this.year)

    fun ApiMessage.toProtoJson(): String = JsonFormat.printer().print(toProto())

    fun String.fromProtoJson(): ApiMessage {
        val builder = ApiMessageProto.newBuilder()
        JsonFormat.parser().ignoringUnknownFields().merge(this, builder)
        return builder.build().fromProto()
    }

    /**
     * Utils
     */

    fun <F, T> List<F>.fromProtoAsNel(mapper: (F) -> T): NonEmptyList<T> = map(mapper).toNonEmptyListOrNull()
        ?: throw IndexOutOfBoundsException("Empty list doesn't contain element at index 0.")
}