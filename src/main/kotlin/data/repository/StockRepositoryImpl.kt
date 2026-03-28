package data.repository

import domain.model.StockSnapshot
import domain.repository.StockRepository
import infrastructure.database.tables.StockSnapshotTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime
import java.util.*

class StockRepositoryImpl(private val database: Database) : StockRepository {

    override suspend fun updateSnapshot(snapshot: StockSnapshot): Boolean = dbQuery {
        val existing = StockSnapshotTable.selectAll()
            .where { StockSnapshotTable.productId eq UUID.fromString(snapshot.productId) }
            .singleOrNull()

        if (existing == null) {
            StockSnapshotTable.insert {
                it[id] = UUID.randomUUID()
                it[productId] = UUID.fromString(snapshot.productId)
                it[deviceId] = UUID.fromString(snapshot.deviceId)
                it[currentWeight] = snapshot.currentWeight.toBigDecimal()
                it[currentStock] = snapshot.currentStock
                it[status] = snapshot.status
                it[snapshotTime] = LocalDateTime.parse(snapshot.snapshotTime)
            }
            true
        } else {
            StockSnapshotTable.update({ StockSnapshotTable.productId eq UUID.fromString(snapshot.productId) }) {
                it[deviceId] = UUID.fromString(snapshot.deviceId)
                it[currentWeight] = snapshot.currentWeight.toBigDecimal()
                it[currentStock] = snapshot.currentStock
                it[status] = snapshot.status
                it[snapshotTime] = LocalDateTime.parse(snapshot.snapshotTime)
            } > 0
        }
    }

    override suspend fun getCurrentStocks(): List<StockSnapshot> = dbQuery {
        StockSnapshotTable.selectAll().map(::toStockSnapshot)
    }

    override suspend fun getByProduct(productId: String): StockSnapshot? = dbQuery {
        StockSnapshotTable.selectAll()
            .where { StockSnapshotTable.productId eq UUID.fromString(productId) }
            .map(::toStockSnapshot)
            .singleOrNull()
    }

    private fun toStockSnapshot(row: ResultRow) = StockSnapshot(
        id = row[StockSnapshotTable.id].toString(),
        productId = row[StockSnapshotTable.productId].toString(),
        deviceId = row[StockSnapshotTable.deviceId].toString(),
        currentWeight = row[StockSnapshotTable.currentWeight].toDouble(),
        currentStock = row[StockSnapshotTable.currentStock],
        status = row[StockSnapshotTable.status],
        snapshotTime = row[StockSnapshotTable.snapshotTime].toString()
    )

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { block() }
}
