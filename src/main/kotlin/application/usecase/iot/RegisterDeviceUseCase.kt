package application.usecase.iot

import domain.model.IOTDevice
import domain.repository.IOTDeviceRepository
import domain.repository.ProductRepository

class RegisterDeviceUseCase(
    private val repository: IOTDeviceRepository,
    private val productRepository: ProductRepository // <-- Tambahin ini
) {
    suspend operator fun invoke(
        deviceCode: String,
        deviceName: String,
        productId: String,
        status: String
    ): IOTDevice {
        // 1. Validasi Input Dasar
        require(deviceCode.isNotBlank()) { "Device code cannot be empty" }
        require(deviceName.isNotBlank()) { "Device name cannot be empty" }
        require(productId.isNotBlank()) { "Product ID is required" }

        // 2. Business Logic: Pastikan Produk yang dipasangkan beneran ada
        productRepository.findById(productId) 
            ?: throw IllegalArgumentException("Product with ID $productId not found")

        // 3. Business Logic: Cek kode alat duplikat
        if (repository.findByCode(deviceCode) != null) {
            throw IllegalArgumentException("Device code already exists")
        }

        // 4. Create Entity & Save
        val device = IOTDevice(
            deviceCode = deviceCode,
            deviceName = deviceName,
            productId = productId,
            status = status
        )

        return repository.create(device)
    }
}
