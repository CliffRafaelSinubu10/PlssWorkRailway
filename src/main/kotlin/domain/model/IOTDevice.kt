package domain.model

import kotlinx.serialization.Serializable

@Serializable
data class IOTDevice(
    val id: String? = null,
    val deviceCode: String,
    val deviceName: String,
    val productId: String,
    val status: String,
    val lastSeenAt: String? = null
)
