package jpm.movie.core

/**
 * Abstraction of a given queue or topic, where the [QueueBridge] will connect to, in order to
 * consume the messages provided by the Data Provider's interface (S3 bucket)
 */
data class QueueSubscriptionConfig(
    val name: String
)
