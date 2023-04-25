package jpm.movie.core

import com.google.inject.Inject
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.util.logging.Logger
import jpm.movie.Log
import org.flywaydb.core.Flyway

sealed class DataProviderService @Inject constructor(
    private val persistence: DBBridge,
    private val queue: QueueBridge,
    @Log private val logger: Logger,
) : DataProviderAPI {

    init {
        initDatabase()
    }

    override suspend fun pushData(rawMovieData: String) {
        TODO("Not yet implemented")
    }

    companion object {
        fun initDatabase() {
            val hikariConfig = HikariConfig("db.properties")
            val dataSource = HikariDataSource(hikariConfig)

            val flyway = Flyway.configure().dataSource(dataSource).load()
            flyway.migrate()
        }
    }
}

fun main() {
    DataProviderService.initDatabase()
}