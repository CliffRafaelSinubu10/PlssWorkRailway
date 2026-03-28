package domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SensorReading(
    val id: String? = null,
    val deviceId: String,
    val productId: String,
    val rawWeight: Double,
    val filteredWeight: Double,
    val estimatedStock: Int,
    val validationStatus: String,
    val noiseLevel: Double,
    val isAnomaly: Boolean,
    val recordedAt: String
)
