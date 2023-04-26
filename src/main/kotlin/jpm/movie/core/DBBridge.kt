package jpm.movie.core

import arrow.core.EitherNel
import jpm.movie.model.Movie
import jpm.movie.model.MovieSvcError
import jpm.movie.model.ValidatedRequest

sealed interface DBBridge {
    fun persistMovie(movie: Movie): EitherNel<MovieSvcError, Movie>

    fun seekMovie(query: ValidatedRequest): EitherNel<MovieSvcError, Set<Movie>>
}