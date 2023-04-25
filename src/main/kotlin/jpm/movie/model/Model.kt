package jpm.movie.model

@JvmInline
value class Year(val year: Int)

@JvmInline
value class MovieName(val name: String)

@JvmInline
value class CastMember(val name: String)

enum class Genre {
    ACTION,
    ADVENTURE,
    ANIMATED,
    BIOGRAPHY,
    COMEDY,
    CRIME,
    DANCE,
    DISASTER,
    DOCUMENTARY,
    DRAMA,
    EROTIC,
    FAMILY,
    FANTASY,
    FOUND_FOOTAGE,
    HISTORICAL,
    HORROR,
    INDEPENDENT,
    LEGAL,
    LIVE_ACTION,
    MARTIAL_ARTS,
    MUSICAL,
    MYSTERY,
    NOIR,
    PERFORMANCE,
    POLITICAL,
    ROMANCE,
    SATIRE,
    SCIENCE_FICTION,
    SHORT,
    SILENT,
    SLASHER,
    SPORTS,
    SPY,
    SUPERHERO,
    SUPERNATURAL,
    SUSPENSE,
    TEEN,
    THRILLER,
    WAR,
    WESTERN
}

data class Movie(
    val year: Year?,
    val name: MovieName?,
    val cast: Set<CastMember>?,
    val genre: Set<Genre>?
)