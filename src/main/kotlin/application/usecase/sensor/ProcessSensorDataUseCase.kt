package application.usecase.sensor

import domain.model.SensorReading
import domain.model.StockSnapshot
import domain.repository.IOTDeviceRepository
import domain.repository.ProductRepository
import domain.repository.SensorRepository
import domain.repository.StockRepository
import java.time.LocalDateTime
import kotlin.math.floor

class ProcessSensorDataUseCase(
    private val sensorRepository: SensorRepository,
    private val stockRepository: StockRepository,
    private val deviceRepository: IOTDeviceRepository,
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(
        deviceCode: String,
        rawWeight: Double,
        filteredWeight: Double,
        timestamp: String
    ): SensorReading {
        // 1. Validasi: Pastikan alat IoT terdaftar
        val device = deviceRepository.findByCode(deviceCode)
            ?: throw IllegalArgumentException("Device with code $deviceCode not found")

        // 2. Ambil data produk terkait
        val product = productRepository.findById(device.productId)
            ?: throw Exception("Product for device $deviceCode not found")

        // 3. Perhitungan Stok: floor(filtered_weight / unit_weight)
        val estimatedStock = floor(filteredWeight / product.unitWeight).toInt()

        // 4. Tentukan Validation Status (Simpel: Reject berat negatif)
        val status = when {
            filteredWeight < 0 -> "INVALID"
            else -> "VALID"
        }

        if (status == "INVALID") {
            throw IllegalArgumentException("Weight cannot be negative")
        }

        // 5. Simpan data ke sensor_readings
        val reading = SensorReading(
            deviceId = device.id!!,
            productId = product.id!!,
            rawWeight = rawWeight,
            filteredWeight = filteredWeight,
            estimatedStock = estimatedStock,
            validationStatus = status,
            recordedAt = timestamp
        )
        val savedReading = sensorRepository.saveReading(reading)

        // 6. Update Stock Snapshot
        val snapshot = StockSnapshot(
            productId = product.id!!,
            deviceId = device.id!!,
            currentWeight = filteredWeight,
            currentStock = estimatedStock,
            status = status,
            snapshotTime = timestamp
        )
        stockRepository.updateSnapshot(snapshot)

        // 7. Update Last Seen (Cara 2)
        deviceRepository.updateLastSeen(device.id!!)

        return savedReading
    }
}
