package jpm.movie.core

import com.google.inject.Inject
import com.google.inject.Singleton
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.request.header
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.time.Instant
import java.util.logging.Logger
import jpm.movie.Log
import jpm.movie.model.Codecs
import jpm.movie.model.MovieResponse
import jpm.movie.model.QueryResult
import jpm.movie.model.RawRequest
import jpm.movie.model.RequestFailure
import jpm.movie.model.ValidationError

/**
 * Embedded Http server, that provides the APIs the service can be called.
 */
@Singleton
class HttpApiImpl @Inject constructor(
    private val httpConfig: HttpApiConfig,
    private val queryService: QueryService,
    @Log private val logger: Logger,
) : HttpApi, Service, Codecs {

    private var applicationEngine: NettyApplicationEngine? = null

    init {
        logger.info("Starting HTTP API...")
        start()
    }

    override fun start() {
        applicationEngine = embeddedServer(Netty, port = httpConfig.port, host = httpConfig.host) {
            routing {
                get("/healthcheck") {
                    call.respondText(Instant.now().toEpochMilli().toString(), ContentType.Text.Plain, HttpStatusCode.OK)
                }
                get("/movies") {
                    if (call.request.header(HttpHeaders.Accept) != "application/json")
                    // Indicates that the response could not be processed by the client.
                        call.response.status(HttpStatusCode.NotAcceptable)
                    else if (call.request.header(HttpHeaders.ContentType) != "application/json") {
                        // Indicates the content type that is used in the body of the request is not supported
                        call.response.status(HttpStatusCode.BadRequest)
                    } else {
                        val years = call.request.queryParameters["years"]?.split(",")?.toSet() ?: emptySet()
                        val movieNames = call.request.queryParameters["titles"]?.split(",")?.toSet() ?: emptySet()
                        val castMember = call.request.queryParameters["cast"]?.split(",")?.toSet() ?: emptySet()
                        val genres = call.request.queryParameters["genres"]?.split(",")?.toSet() ?: emptySet()

                        logger.info("years: [$years]; movies: [$movieNames]; cast: [$castMember]; genres: [$genres]")

                        queryService.queryMovies(RawRequest(years, movieNames, castMember, genres))
                            .also { logger.info { "Response: [$it]" } }
                            .let {
                                call.respondText(it.toProtoJson(), ContentType.Application.Json, generateStatusCode(it))
                            }
                    }
                }
            }
        }.start(wait = true)
    }

    override fun stop() {
        applicationEngine?.stop()
    }
}

private fun identifyRequestFailureStatusCode(failure: RequestFailure): HttpStatusCode {
    // based on the different Errors, the status code can be controlled through this function
    return if (failure.errors.size == 1 && failure.errors.first() is ValidationError) {
        HttpStatusCode.BadRequest
    } else {
        HttpStatusCode.InternalServerError
    }
}

private fun generateStatusCode(response: MovieResponse) = when (response) {
    is RequestFailure ->
        identifyRequestFailureStatusCode(response)

    is QueryResult ->
        if (response.movies.isEmpty()) HttpStatusCode.NotFound else HttpStatusCode.OK
}