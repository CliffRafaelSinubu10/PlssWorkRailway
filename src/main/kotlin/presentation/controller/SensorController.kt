package presentation.controller

import application.usecase.sensor.ProcessSensorReadingUseCase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import presentation.dto.request.SensorDataRequest
import presentation.dto.response.BaseResponse

class SensorController(
    private val processSensorReadingUseCase: ProcessSensorReadingUseCase
) {
    suspend fun processSensorData(call: ApplicationCall) {
        val request = call.receive<SensorDataRequest>()
        try {
            val reading = processSensorReadingUseCase(
                deviceCode = request.device_code,
                rawWeight = request.raw_weight,
                filteredWeight = request.filtered_weight,
                recordedAt = request.recorded_at
            )
            
            val response = BaseResponse(
                success = true,
                data = mapOf(
                    "estimated_stock" to reading.estimatedStock,
                    "validation_status" to reading.validationStatus,
                    "is_anomaly" to reading.isAnomaly
                ),
                message = "Sensor data processed successfully"
            )
            call.respond(HttpStatusCode.OK, response)
        } catch (e: IllegalArgumentException) {
            call.respond(
                HttpStatusCode.BadRequest,
                BaseResponse<Unit>(success = false, message = e.message)
            )
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                BaseResponse<Unit>(success = false, message = "Internal Server Error: ${e.message}")
            )
        }
    }
}
