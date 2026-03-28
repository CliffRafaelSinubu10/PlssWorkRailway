package application.usecase.iot

import domain.model.IOTDevice
import domain.repository.IOTDeviceRepository
import domain.repository.ProductRepository

class UpdateDeviceUseCase(
    private val repository: IOTDeviceRepository,
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(id: String, device: IOTDevice): Boolean {
        require(device.deviceName.isNotBlank()) { "Device name cannot be empty" }
        
        // Business Logic: Pastikan Produk yang dipasangkan beneran ada
        productRepository.findById(device.productId) 
            ?: throw IllegalArgumentException("Product with ID ${device.productId} not found")
        
        return repository.update(id, device)
    }
}
