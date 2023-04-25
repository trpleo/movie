package jpm.movie.core

/**
 * Configuration, that contains the necessary data to connect the predefined queue, where the S3 changes will be
 * propagated.
 */
data class QueueBridgeConfig(
    val region: String = "us-east-1",
    val queueUrlVal: String,
)
