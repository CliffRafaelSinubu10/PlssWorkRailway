package presentation.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class IOTDeviceResponse(
    val id: String,
    val deviceCode: String,
    val deviceName: String,
    val productId: String,
    val status: String,
    val lastSeenAt: String? = null
)
