package presentation.routes

import io.ktor.server.routing.*
import presentation.controller.AuthController

fun Route.authRoutes(authController: AuthController) {
    // 1. PUBLIC ENDPOINTS (No Token Needed)
    route("/api/v1/auth") {
        post("/register") {
            authController.register(call)
        }
        post("/login") {
            authController.login(call)
        }
    }
}
