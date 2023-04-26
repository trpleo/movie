package jpm.movie

import com.google.inject.matcher.Matchers
import jpm.movie.core.DBBridge
import jpm.movie.core.DBBridgeImpl
import jpm.movie.core.DataProvidersAPI
import jpm.movie.core.DataProvidersService
import jpm.movie.core.HttpApi
import jpm.movie.core.HttpApiImpl
import jpm.movie.core.QueryService
import jpm.movie.core.QueryServiceImpl
import jpm.movie.core.QueueBridge
import jpm.movie.core.QueueBridgeImpl
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.newFixedThreadPoolContext


class MovieSvcModule(private val config: MovieSvcConfig) : BindingUtil() {

    private fun bindConfig() {
        bind(config)
        bind(config.httpApiConfig)
        bind(config.dbBridgeConfig)
        bind(config.queueBridgeConfig)
    }

    private fun bindCommon() {
        val maxConcurrency = 8 // parallel execution naturally not limited by this value. see: Coroutines
        val dispatcher =
            newFixedThreadPoolContext(maxConcurrency, "movie-dispatcher")

        bind<DBBridge>().to<DBBridgeImpl>()
        bind<CoroutineContext>().toInstance(dispatcher)
    }

    private fun bindHttpApi() {
        bind<QueryService>().to<QueryServiceImpl>()
        bind<HttpApi>().to<HttpApiImpl>()
    }

    private fun bindDataProviderApi() {
        bind<QueueBridge>().to<QueueBridgeImpl>()
        bind<DataProvidersAPI>().to<DataProvidersService>()
    }

    private fun bindLogger() {
        bindListener(Matchers.any(), SLF4JTypeListener())
    }

    override fun configure() {
        bindConfig()
        bindLogger()
        bindCommon()

        bindDataProviderApi()
        bindHttpApi()
    }
}