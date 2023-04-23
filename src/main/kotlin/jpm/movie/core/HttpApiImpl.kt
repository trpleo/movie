package jpm.movie.core

import com.google.inject.Inject
import com.google.inject.Singleton
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.util.logging.Logger
import jpm.movie.Log

interface HttpApi

/**
 * Embedded Http server, that provides the APIs the service can be called.
 */
@Singleton
class HttpApiImpl @Inject constructor(private val httpConfig: HttpApiConfig, @Log private val logger: Logger) : HttpApi,
    Service {

    private var applicationEngine: NettyApplicationEngine? = null

    init {
        logger.info("Starting HTTP API...")
        start()
    }

    override fun start() {
        applicationEngine = embeddedServer(Netty, port = httpConfig.port, host = httpConfig.host) {
            routing {
                get("/") {
                    call.respondText("Hello, world!")
                }
            }
        }.start(wait = true)
    }

    override fun stop() {
        applicationEngine?.stop()
    }
}