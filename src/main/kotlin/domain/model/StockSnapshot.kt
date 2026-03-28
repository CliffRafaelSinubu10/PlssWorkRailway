package domain.model

import kotlinx.serialization.Serializable

@Serializable
data class StockSnapshot(
    val id: String? = null,
    val productId: String,
    val deviceId: String,
    val currentWeight: Double,
    val currentStock: Int,
    val status: String,
    val snapshotTime: String
)
