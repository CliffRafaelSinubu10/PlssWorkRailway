package application.usecase.iot

import domain.model.IOTDevice
import domain.repository.IOTDeviceRepository

class GetDevicesUseCase(private val repository: IOTDeviceRepository) {
    suspend operator fun invoke(): List<IOTDevice> {
        return repository.getAll()
    }
}
