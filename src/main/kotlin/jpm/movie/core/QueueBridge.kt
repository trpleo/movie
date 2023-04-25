package jpm.movie.core

sealed interface QueueBridge {
    fun subscribe()
}