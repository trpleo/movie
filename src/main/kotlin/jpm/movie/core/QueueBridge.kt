package jpm.movie.core

import arrow.core.EitherNel
import jpm.movie.model.Movie
import jpm.movie.model.MovieSvcError

sealed interface QueueBridge {
    fun subscribe(callback: (Movie) -> EitherNel<MovieSvcError, Movie>)
}