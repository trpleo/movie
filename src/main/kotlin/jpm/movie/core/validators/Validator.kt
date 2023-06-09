package jpm.movie.core.validators

import arrow.core.EitherNel
import arrow.core.leftNel
import arrow.core.right

/**
 * Generic validation function.
 *
 * @param toCheck the value to validate
 * @param invalidWhen the validation function
 * @param transformer the transformation function
 * @param transformOnly flag to skip validation and perform only transformation
 * @param cause the error to return if [invalidWhen] is true or in case of any exceptions
 */
fun <T, E, R> validator(
    toCheck: T,
    invalidWhen: (T) -> Boolean,
    transformer: (T) -> R,
    cause: () -> E,
    transformOnly: Boolean = false
): EitherNel<E, R> =
    runCatching {
        if (!transformOnly && invalidWhen(toCheck)) {
            throw IllegalArgumentException()
        } else {
            transformer(toCheck)
        }
    }.fold(
        { it.right() },
        { cause().leftNel() },
    )