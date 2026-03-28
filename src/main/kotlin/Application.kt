import infrastructure.di.AppComponent
import io.ktor.server.application.*
import plugins.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    // 1. Inisialisasi Dependency Injection
    val appComponent = AppComponent(environment)

    // 2. Ambil config secara aman. Kalau gak ada di yaml, pake default 5 menit.
    val inactivityThreshold = try {
        environment.config.propertyOrNull("iot.inactivityThresholdMinutes")?.getString()?.toLong() ?: 5L
    } catch (e: Exception) {
        5L
    }

    // 3. Start background task (Scheduler)
    launch {
        while (true) {
            delay(1.minutes)
            try {
                appComponent.useCaseModule.checkInactiveDevicesUseCase.invoke(
                    inactivityThresholdMinutes = inactivityThreshold
                )
            } catch (e: Exception) {
                log.error("Background Job Error: ${e.message}")
            }
        }
    }

    // 4. Configure Plugins
    configureSerialization()
    configureMonitoring()
    configureSecurity()
    configureRouting(appComponent)
}
