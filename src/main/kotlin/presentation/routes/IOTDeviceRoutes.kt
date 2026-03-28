package presentation.routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import presentation.controller.IOTDeviceController

fun Route.iotDeviceRoutes(iotDeviceController: IOTDeviceController) {
    // Public endpoint for IoT Device Heartbeat (no JWT)
    post("/api/v1/devices/{deviceCode}/heartbeat") {
        iotDeviceController.updateLastSeen(call)
    }

    authenticate("auth-jwt") { // <-- Protect all other product routes with JWT
        route("/api/v1/devices") {
            post {
                iotDeviceController.registerDevice(call)
            }
            get {
                iotDeviceController.getDevices(call)
            }
            get("/{id}") {
                iotDeviceController.getDeviceById(call)
            }
            put("/{id}") {
                iotDeviceController.updateDevice(call)
            }
            delete("/{id}") {
                iotDeviceController.deleteDevice(call)
            }
        }
    }
}
