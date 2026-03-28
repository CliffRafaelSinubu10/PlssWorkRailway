package application.usecase.sensor

import domain.model.SensorReading
import domain.model.StockSnapshot
import domain.repository.IOTDeviceRepository
import domain.repository.ProductRepository
import domain.repository.SensorRepository
import domain.repository.StockRepository
import java.time.LocalDateTime
import kotlin.math.abs
import kotlin.math.floor

class ProcessSensorReadingUseCase(
    private val sensorRepository: SensorRepository,
    private val stockRepository: StockRepository,
    private val deviceRepository: IOTDeviceRepository,
    private val productRepository: ProductRepository
) {
    private val MAX_WEIGHT_JUMP_PERCENT = 30.0

    suspend operator fun invoke(
        deviceCode: String,
        rawWeight: Double,
        filteredWeight: Double,
        recordedAt: String
    ): SensorReading {
        // 1. Basic Validation
        require(deviceCode.isNotBlank()) { "Device code is required" }
        require(rawWeight >= 0) { "Raw weight cannot be negative" }
        require(filteredWeight >= 0) { "Filtered weight cannot be negative" }

        // 2. Business Rules: Device must be registered
        val device = deviceRepository.findByCode(deviceCode)
            ?: throw IllegalArgumentException("Device not found: $deviceCode")

        val product = productRepository.findById(device.productId)
            ?: throw Exception("Product not associated with device: $deviceCode")

        // 3. Calculate Noise Level
        val noiseLevel = abs(rawWeight - filteredWeight)

        // 4. Anomaly Detection: Compare with previous reading
        val previousReading = sensorRepository.getLatestReading(device.id!!)
        var isAnomaly = false
        var validationStatus = "VALID"

        if (previousReading != null) {
            val prevWeight = previousReading.filteredWeight
            if (prevWeight > 0) {
                val diff = abs(filteredWeight - prevWeight)
                val diffPercent = (diff / prevWeight) * 100
                if (diffPercent > MAX_WEIGHT_JUMP_PERCENT) {
                    validationStatus = "SUSPICIOUS"
                    isAnomaly = true
                }
            }
        }

        // 5. Validation Logic based on Noise
        if (validationStatus == "VALID" && noiseLevel > 1.0) { // Threshold noise 1kg
            validationStatus = "UNSTABLE"
        }

        // 6. Calculate Estimated Stock
        val estimatedStock = floor(filteredWeight / product.unitWeight).toInt()

        // 7. Create Sensor Reading Domain Model
        val reading = SensorReading(
            deviceId = device.id!!,
            productId = product.id!!,
            rawWeight = rawWeight,
            filteredWeight = filteredWeight,
            estimatedStock = estimatedStock,
            validationStatus = validationStatus,
            noiseLevel = noiseLevel,
            isAnomaly = isAnomaly,
            recordedAt = recordedAt
        )

        // 8. Transactional persistence: Save Reading & Update Snapshot
        // Note: RepositoryImpl handles the transaction context
        val savedReading = sensorRepository.saveReading(reading)

        // 9. Update Stock Snapshot ONLY if VALID or UNSTABLE
        if (validationStatus == "VALID" || validationStatus == "UNSTABLE") {
            val snapshot = StockSnapshot(
                productId = product.id!!,
                deviceId = device.id!!,
                currentWeight = filteredWeight,
                currentStock = estimatedStock,
                status = validationStatus,
                snapshotTime = recordedAt
            )
            stockRepository.updateSnapshot(snapshot)
        }

        // 10. Update Last Seen (Cara 2)
        deviceRepository.updateLastSeen(device.id!!)

        return savedReading
    }
}
