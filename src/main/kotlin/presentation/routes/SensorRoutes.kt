package presentation.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import presentation.controller.SensorController

fun Route.sensorRoutes(sensorController: SensorController) {
    route("/api/v1/sensor-readings") { // Sesuai Sensor_reading_plan.md
        post {
            sensorController.processSensorData(call)
        }
    }
}
