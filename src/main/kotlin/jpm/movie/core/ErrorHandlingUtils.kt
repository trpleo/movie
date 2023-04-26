package jpm.movie.core

import arrow.core.Either
import arrow.core.EitherNel
import jpm.movie.model.MovieSvcError
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield

suspend fun <R> gradualErrorHandler(
    logger: java.util.logging.Logger,
    remainingRetries: Long,
    sideEffectF: suspend () -> EitherNel<MovieSvcError, R>
): EitherNel<MovieSvcError, R> = gradualErrorHandler(logger, remainingRetries, ::retryToWait, sideEffectF)

tailrec suspend fun <R> gradualErrorHandler(
    logger: java.util.logging.Logger,
    remainingRetries: Long,
    timestampCalculator: (Long) -> Duration,
    sideEffectF: suspend () -> EitherNel<MovieSvcError, R>
): EitherNel<MovieSvcError, R> =
    when (val result = sideEffectF()) {
        is Either.Left -> {
            val remains = remainingRetries - 1
            val waitForNexExecution = timestampCalculator(remains)
            logger.info { "Execution failed in gradualErrorHandler. Cause: [$result]. Next retry in [$waitForNexExecution]." }
            delay(waitForNexExecution)
            yield()
            gradualErrorHandler(logger, remains, timestampCalculator, sideEffectF)
        }

        is Either.Right -> result
    }

private fun retryToWait(remainingRetries: Long, maxWait: Duration = 5.minutes): Duration =
    when {
        remainingRetries < 1 -> maxWait
        else -> (maxWait.inWholeMilliseconds / remainingRetries).toDuration(DurationUnit.MILLISECONDS)
    }