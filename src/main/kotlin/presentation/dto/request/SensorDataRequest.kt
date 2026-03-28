package presentation.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class SensorDataRequest(
    val device_code: String,
    val filtered_weight: Double,
    val raw_weight: Double,
    val recorded_at: String
)
