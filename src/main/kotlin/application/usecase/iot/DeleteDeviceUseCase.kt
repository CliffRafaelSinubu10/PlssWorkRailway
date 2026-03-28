package application.usecase.iot

import domain.repository.IOTDeviceRepository

class DeleteDeviceUseCase(private val repository: IOTDeviceRepository) {
    suspend operator fun invoke(id: String): Boolean {
        return repository.delete(id)
    }
}
