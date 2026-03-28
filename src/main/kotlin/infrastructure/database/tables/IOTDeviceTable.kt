package infrastructure.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object IOTDeviceTable : Table("iot_devices") {
    val id = uuid("id")
    val deviceCode = varchar("device_code", 100).uniqueIndex()
    val deviceName = varchar("device_name", 100)
    val productId = uuid("product_id").references(ProductTable.id)
    val status = varchar("status", 50)
    val lastSeenAt = datetime("last_seen_at").nullable()
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(id)
}
