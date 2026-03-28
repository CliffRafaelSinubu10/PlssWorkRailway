package presentation.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class IOTDeviceRequest(
    val deviceCode: String,
    val deviceName: String,
    val productId: String,
    val status: String
)
