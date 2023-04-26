package jpm.movie

import jpm.movie.core.DbBridgeConfig
import jpm.movie.core.HttpApiConfig
import jpm.movie.core.QueueBridgeConfig

data class MovieSvcConfig(
    val httpApiConfig: HttpApiConfig,
    val dbBridgeConfig: DbBridgeConfig,
    val queueBridgeConfig: QueueBridgeConfig,
)