package presentation.routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import presentation.controller.InventoryController

fun Route.inventoryRoutes(inventoryController: InventoryController) {
    route("/api/v1/inventory") {
        // Public/IoT scan endpoint
        post("/scan") {
            inventoryController.processScan(call)
        }

        authenticate("auth-jwt") {
            // Admin only registration of tags
            post("/register-tag") {
                inventoryController.registerTag(call)
            }
            
            // Delete a tag from a product
            delete("/tags/{productId}") {
                inventoryController.deleteTag(call)
            }
            
            // Check if a product has a registered tag
            get("/tags/{productId}") {
                inventoryController.getTagByProduct(call)
            }
            
            // Dashboard data
            get("/dashboard") {
                inventoryController.getDashboard(call)
            }
            
            // Set IoT Operation Mode (Pintar)
            post("/iot-mode") {
                inventoryController.setIotMode(call)
            }
            
            // History per product
            get("/{productId}/history") {
                inventoryController.getHistory(call)
            }
        }
    }
}
