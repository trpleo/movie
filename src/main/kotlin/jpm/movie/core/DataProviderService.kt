package jpm.movie.core

import com.google.inject.Inject
import java.util.logging.Logger
import jpm.movie.Log

sealed class DataProviderService @Inject constructor(
    private val persistence: DBBridge,
    private val queue: QueueBridge,
    @Log private val logger: Logger,
) : DataProviderAPI {

    init {

    }

    override suspend fun pushData(rawMovieData: String) {
        TODO("Not yet implemented")
    }
}