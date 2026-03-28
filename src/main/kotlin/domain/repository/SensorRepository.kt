package domain.repository

import domain.model.SensorReading

interface SensorRepository {
    suspend fun saveReading(reading: SensorReading): SensorReading
    suspend fun getHistory(productId: String, limit: Int): List<SensorReading>
    suspend fun getLatestReading(deviceId: String): SensorReading?
}
