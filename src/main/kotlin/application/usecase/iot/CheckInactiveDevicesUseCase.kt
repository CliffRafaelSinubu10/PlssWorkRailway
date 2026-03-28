package application.usecase.iot

import domain.repository.IOTDeviceRepository
import java.time.LocalDateTime
import java.time.Duration

class CheckInactiveDevicesUseCase(private val repository: IOTDeviceRepository) {
    suspend operator fun invoke(inactivityThresholdMinutes: Long = 5) {
        val devices = repository.getAll()
        val now = LocalDateTime.now()

        devices.forEach { device ->
            device.lastSeenAt?.let { lastSeenAtString ->
                val lastSeenTime = LocalDateTime.parse(lastSeenAtString)
                val duration = Duration.between(lastSeenTime, now)
                
                if (duration.toMinutes() > inactivityThresholdMinutes && device.status != "INACTIVE") {
                    // Update status to INACTIVE
                    val inactiveDevice = device.copy(status = "INACTIVE")
                    repository.update(device.id!!, inactiveDevice)
                }
            } ?: run { // Handle case where lastSeenAt is null (device never reported)
                if (device.status != "INACTIVE") {
                    val inactiveDevice = device.copy(status = "INACTIVE")
                    repository.update(device.id!!, inactiveDevice)
                }
            }
        }
    }
}
