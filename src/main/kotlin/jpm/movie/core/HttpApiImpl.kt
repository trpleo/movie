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
import java.time.Instant
import java.util.logging.Logger
import jpm.movie.Log

/**
 * Embedded Http server, that provides the APIs the service can be called.
 */
@Singleton
class HttpApiImpl @Inject constructor(
    private val httpConfig: HttpApiConfig,
//    @Inject private val queryService: QueryService,
    @Log private val logger: Logger,
) : HttpApi, Service {

    private var applicationEngine: NettyApplicationEngine? = null

    init {
        logger.info("Starting HTTP API...")
        start()
    }

    override fun start() {
        applicationEngine = embeddedServer(Netty, port = httpConfig.port, host = httpConfig.host) {
            routing {
                get("/healthcheck") {
                    call.respondText(Instant.now().toEpochMilli().toString())
                }
                get("/movies") {
                    val years = call.request.queryParameters["years"]
                    val movieNames = call.request.queryParameters["titles"]
                    val castMember = call.request.queryParameters["cast"]
                    val genres = call.request.queryParameters["genres"]

                    logger.info("years: [$years]; movies: [$movieNames]; cast: [$castMember]; genres: [$genres]")
                }
            }
        }.start(wait = true)
    }

    override fun stop() {
        applicationEngine?.stop()
    }
}