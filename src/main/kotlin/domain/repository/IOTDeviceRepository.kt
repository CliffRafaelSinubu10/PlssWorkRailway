package domain.repository

import domain.model.IOTDevice

interface IOTDeviceRepository {
    suspend fun create(device: IOTDevice): IOTDevice
    suspend fun findByCode(deviceCode: String): IOTDevice?
    suspend fun findById(id: String): IOTDevice?
    suspend fun getAll(): List<IOTDevice>
    suspend fun update(id: String, device: IOTDevice): Boolean
    suspend fun delete(id: String): Boolean
    suspend fun updateLastSeen(id: String): Boolean
}
