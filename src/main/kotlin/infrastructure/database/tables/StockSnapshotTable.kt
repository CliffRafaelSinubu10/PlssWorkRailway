package infrastructure.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object StockSnapshotTable : Table("stock_snapshots") {
    val id = uuid("id")
    val productId = uuid("product_id").references(ProductTable.id)
    val deviceId = uuid("device_id").references(IOTDeviceTable.id)
    val currentWeight = decimal("current_weight", 10, 3)
    val currentStock = integer("current_stock")
    val status = varchar("status", 50)
    val snapshotTime = datetime("snapshot_time")
    val createdAt = datetime("created_at").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(id)
}
