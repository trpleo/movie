package jpm.movie

import com.google.inject.matcher.Matchers
import jpm.movie.core.HttpApi
import jpm.movie.core.HttpApiImpl
import jpm.movie.core.QueryService
import jpm.movie.core.QueryServiceImpl


class MovieSvcModule(private val config: MovieSvcConfig) : BindingUtil() {

    private fun bindConfig() {
        bind(config)
        bind(config.httpApiConfig)
    }

    private fun bindHttpApi() {
        bind<QueryService>().to<QueryServiceImpl>()
        bind<HttpApi>().to<HttpApiImpl>()
    }

    private fun bindLogger() {
        bindListener(Matchers.any(), SLF4JTypeListener())
    }

    override fun configure() {
        bindConfig()
        bindHttpApi()
        bindLogger()
    }
}