package infrastructure.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object SensorReadingTable : Table("sensor_readings") {
    val id = uuid("id")
    val deviceId = uuid("device_id").references(IOTDeviceTable.id)
    val productId = uuid("product_id").references(ProductTable.id)
    val rawWeight = decimal("raw_weight", 10, 3)
    val filteredWeight = decimal("filtered_weight", 10, 3)
    val estimatedStock = integer("estimated_stock")
    val validationStatus = varchar("validation_status", 50)
    val noiseLevel = decimal("noise_level", 10, 3)
    val isAnomaly = bool("is_anomaly").default(false)
    val recordedAt = datetime("recorded_at")
    val createdAt = datetime("created_at").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(id)
}
