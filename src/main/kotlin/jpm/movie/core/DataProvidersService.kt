package jpm.movie.core

import arrow.core.EitherNel
import arrow.core.nel
import arrow.core.right
import com.google.inject.Inject
import com.google.inject.Singleton
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.util.UUID
import java.util.logging.Logger
import jpm.movie.Log
import jpm.movie.model.GeneralError
import jpm.movie.model.MovieSvcError
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.output.MigrateResult
import org.jooq.impl.DSL

/**
 * This service is responsible for the highest level to process the incoming events from the Amazon SQS (originated
 * from AWS S3 buckets).
 *
 * This service processes the incoming messages, validates and transform them, and after persist them into the
 * persistent DB, in order to make it searchable. This is the write side of the Movie Service.
 */
@Singleton
class DataProvidersService @Inject constructor(
    private val persistence: DBBridge,
    private val queue: QueueBridge,
    @Log private val logger: Logger,
) : DataProvidersAPI {

    init {
        initDatabase()
            .map { queue.subscribe(persistence::persistMovie) }
            .map { logger.info { "Data Provider's API is up..." } }
    }

    companion object {
        fun initDatabase(): EitherNel<MovieSvcError, MigrateResult> = EitherNel.catch {
            val hikariConfig = HikariConfig("db.properties")
            val dataSource = HikariDataSource(hikariConfig)

            val flyway = Flyway.configure().dataSource(dataSource).load()
            return flyway.migrate().right()
        }.mapLeft { GeneralError("Database Initiation failed with: [${it.message}]").nel() }
    }
}

fun main() {
    DataProvidersService.initDatabase()
}

data class Actor(
    val id: UUID = UUID.randomUUID(),
    val name: String,
) {
    companion object {
        val TABLE = DSL.table("actors")
        val ID = DSL.field("id", UUID::class.java)
        val USER_NAME = DSL.field("user_name", String::class.java)
    }
}