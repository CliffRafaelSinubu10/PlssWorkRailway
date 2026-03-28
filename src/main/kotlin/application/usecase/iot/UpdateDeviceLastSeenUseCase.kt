package application.usecase.iot

import domain.repository.IOTDeviceRepository

class UpdateDeviceLastSeenUseCase(private val repository: IOTDeviceRepository) {
    suspend operator fun invoke(deviceCode: String): Boolean {
        // 1. Validasi: Pastikan deviceCode tidak kosong
        require(deviceCode.isNotBlank()) { "Device code cannot be empty" }

        // 2. Business Logic: Cari device berdasarkan kode
        val device = repository.findByCode(deviceCode)
            ?: throw IllegalArgumentException("Device with code $deviceCode not found")

        // 3. Update lastSeenAt
        return repository.updateLastSeen(device.id!!)
    }
}
