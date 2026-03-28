package application.usecase.iot

import domain.model.IOTDevice
import domain.repository.IOTDeviceRepository

class GetDeviceByIdUseCase(private val repository: IOTDeviceRepository) {
    suspend operator fun invoke(id: String): IOTDevice? {
        return repository.findById(id)
    }
}
