package presentation.controller

import application.usecase.iot.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import presentation.dto.request.IOTDeviceRequest
import presentation.dto.response.BaseResponse
import presentation.dto.response.IOTDeviceResponse
import domain.model.IOTDevice

class IOTDeviceController(
    private val registerDeviceUseCase: RegisterDeviceUseCase,
    private val getDevicesUseCase: GetDevicesUseCase,
    private val getDeviceByIdUseCase: GetDeviceByIdUseCase,
    private val updateDeviceUseCase: UpdateDeviceUseCase,
    private val deleteDeviceUseCase: DeleteDeviceUseCase,
    private val updateDeviceLastSeenUseCase: UpdateDeviceLastSeenUseCase // <-- Tambahin ini
) {
    suspend fun registerDevice(call: ApplicationCall) {
        val request = call.receive<IOTDeviceRequest>()
        try {
            val device = registerDeviceUseCase(
                deviceCode = request.deviceCode,
                deviceName = request.deviceName,
                productId = request.productId,
                status = request.status
            )
            val response = BaseResponse(
                success = true,
                data = IOTDeviceResponse(
                    id = device.id!!,
                    deviceCode = device.deviceCode,
                    deviceName = device.deviceName,
                    productId = device.productId,
                    status = device.status,
                    lastSeenAt = device.lastSeenAt
                ),
                message = "Device registered successfully"
            )
            call.respond(HttpStatusCode.Created, response)
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.Conflict, BaseResponse<Unit>(success = false, message = e.message))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, BaseResponse<Unit>(success = false, message = "Internal Server Error"))
        }
    }

    suspend fun getDevices(call: ApplicationCall) {
        try {
            val devices = getDevicesUseCase()
            val deviceResponses = devices.map {
                IOTDeviceResponse(
                    id = it.id!!,
                    deviceCode = it.deviceCode,
                    deviceName = it.deviceName,
                    productId = it.productId,
                    status = it.status,
                    lastSeenAt = it.lastSeenAt
                )
            }
            call.respond(HttpStatusCode.OK, BaseResponse(success = true, data = deviceResponses))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, BaseResponse<Unit>(success = false, message = "Internal Server Error"))
        }
    }

    suspend fun getDeviceById(call: ApplicationCall) {
        val id = call.parameters["id"] ?: return call.respond(HttpStatusCode.BadRequest, BaseResponse<Unit>(success = false, message = "ID is required"))
        try {
            val device = getDeviceByIdUseCase(id)
            if (device != null) {
                call.respond(HttpStatusCode.OK, BaseResponse(
                    success = true,
                    data = IOTDeviceResponse(
                        id = device.id!!,
                        deviceCode = device.deviceCode,
                        deviceName = device.deviceName,
                        productId = device.productId,
                        status = device.status,
                        lastSeenAt = device.lastSeenAt
                    )
                ))
            } else {
                call.respond(HttpStatusCode.NotFound, BaseResponse<Unit>(success = false, message = "Device not found"))
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, BaseResponse<Unit>(success = false, message = "Internal Server Error"))
        }
    }

    suspend fun updateDevice(call: ApplicationCall) {
        val id = call.parameters["id"] ?: return call.respond(HttpStatusCode.BadRequest, BaseResponse<Unit>(success = false, message = "ID is required"))
        val request = call.receive<IOTDeviceRequest>()
        try {
            val success = updateDeviceUseCase(id, IOTDevice(
                deviceCode = request.deviceCode,
                deviceName = request.deviceName,
                productId = request.productId,
                status = request.status
            ))
            if (success) {
                call.respond(HttpStatusCode.OK, BaseResponse<Unit>(success = true, message = "Device updated successfully"))
            } else {
                call.respond(HttpStatusCode.NotFound, BaseResponse<Unit>(success = false, message = "Device not found"))
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, BaseResponse<Unit>(success = false, message = "Internal Server Error"))
        }
    }

    suspend fun deleteDevice(call: ApplicationCall) {
        val id = call.parameters["id"] ?: return call.respond(HttpStatusCode.BadRequest, BaseResponse<Unit>(success = false, message = "ID is required"))
        try {
            val success = deleteDeviceUseCase(id)
            if (success) {
                call.respond(HttpStatusCode.OK, BaseResponse<Unit>(success = true, message = "Device deleted successfully"))
            } else {
                call.respond(HttpStatusCode.NotFound, BaseResponse<Unit>(success = false, message = "Device not found"))
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, BaseResponse<Unit>(success = false, message = "Internal Server Error"))
        }
    }

    // <-- Fungsi baru untuk Heartbeat
    suspend fun updateLastSeen(call: ApplicationCall) {
        val deviceCode = call.parameters["deviceCode"] ?: return call.respond(
            HttpStatusCode.BadRequest, BaseResponse<Unit>(success = false, message = "Device code is required")
        )
        try {
            val updated = updateDeviceLastSeenUseCase(deviceCode)
            if (updated) {
                call.respond(HttpStatusCode.OK, BaseResponse<Unit>(success = true, message = "Device last seen updated"))
            } else {
                call.respond(HttpStatusCode.NotFound, BaseResponse<Unit>(success = false, message = "Device not found"))
            }
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.NotFound, BaseResponse<Unit>(success = false, message = e.message))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, BaseResponse<Unit>(success = false, message = "Internal Server Error: ${e.message}"))
        }
    }
}
