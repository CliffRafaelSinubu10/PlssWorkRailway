package infrastructure.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import infrastructure.database.tables.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init(): Database {
        // Railway/Production: Force read from environment variables first
        val jdbcUrl = System.getenv("DATABASE_URL") ?: "jdbc:postgresql://ep-rapid-haze-ao07neao-pooler.c-2.ap-southeast-1.aws.neon.tech/neondb?sslmode=require"
        val user = System.getenv("DATABASE_USER") ?: "neondb_owner"
        val password = System.getenv("DATABASE_PASSWORD") ?: "npg_2okqATC7Nrbm"

        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            this.jdbcUrl = jdbcUrl
            username = user
            this.password = password
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            
            // Neon/Cloud Specific Optimizations
            addDataSourceProperty("cachePrepStmts", "true")
            addDataSourceProperty("prepStmtCacheSize", "250")
            addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
            
            validate()
        }

        val dataSource = HikariDataSource(config)
        val database = Database.connect(dataSource)

        transaction(database) {
            SchemaUtils.create(
                AdminTable,
                ProductTable,
                ProductRfidTagTable,
                InventoryEventTable,
                InventorySnapshotTable,
                DailyAggregateTable,
                ForecastingResultTable
            )
        }
        
        return database
    }
}
