package domain.repository

import domain.model.StockSnapshot

interface StockRepository {
    suspend fun updateSnapshot(snapshot: StockSnapshot): Boolean
    suspend fun getCurrentStocks(): List<StockSnapshot>
    suspend fun getByProduct(productId: String): StockSnapshot?
}
