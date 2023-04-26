package jpm.movie

import com.google.inject.Guice
import com.google.inject.Injector
import jpm.movie.core.DataProvidersAPI
import jpm.movie.core.DbBridgeConfig
import jpm.movie.core.HttpApi
import jpm.movie.core.HttpApiConfig
import jpm.movie.core.QueueBridgeConfig

fun main() {
    fun buildConfig(): MovieSvcConfig {
        // todo: add config loaders
        return MovieSvcConfig(
            HttpApiConfig(),
            DbBridgeConfig(),
            QueueBridgeConfig(),
        )
    }

    val config = buildConfig()
    val injector: Injector = Guice.createInjector(MovieSvcModule(config))

    injector.getInstance(DataProvidersAPI::class.java)
    injector.getInstance(HttpApi::class.java)
}