import infrastructure.di.AppComponent
import io.ktor.server.application.*
import plugins.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    // Set Timezone to WITA (Manado / UTC+8) globally
    java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Asia/Makassar"))
    
    val port = System.getenv("PORT")?.toInt() ?: 8080
    embeddedServer(Netty, port = port, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    log.info("Inventory RFID System Backend (V5.5 - FINAL STABLE) is starting...")
    val appComponent = AppComponent(environment)

    // 2. Configure Plugins
    configureSerialization()
    configureMonitoring()
    configureSecurity()
    configureRouting(appComponent)
    
    log.info("Inventory RFID System Backend (V4.0) is running...")
}
