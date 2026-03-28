# 🧠 Sensor Reading Feature — Implementation Plan (IoT + Ktor Backend)

---

# 🎯 Tujuan

Menerima data dari IoT, memvalidasi kualitas data, menghitung estimasi stok, mendeteksi anomaly, menyimpan histori, dan memperbarui kondisi stok terbaru.

---

# 🧱 Outcome System

Setiap data masuk harus menghasilkan:

* Validasi device
* Estimasi stock
* Validation status
* Anomaly detection
* Data tersimpan di history (`sensor_readings`)
* Snapshot terupdate (`stock_snapshots`)

---

# 🌐 Endpoint

```
POST /api/v1/sensor-readings
```

---

# 📥 Request DTO

```json
{
  "device_code": "ESP32-001",
  "raw_weight": 12.8,
  "filtered_weight": 12.5,
  "recorded_at": "2026-03-28T12:00:00Z"
}
```

---

# 📤 Response DTO

```json
{
  "success": true,
  "data": {
    "estimated_stock": 2,
    "validation_status": "VALID",
    "is_anomaly": false
  }
}
```

---

# 🏗️ Architecture Flow

```
Route
 ↓
Controller
 ↓
UseCase (core logic)
 ↓
Repository
 ↓
Database
```

---

# 🧠 Core Use Case: ProcessSensorReadingUseCase

## Flow:

1. Validate request
2. Find device by device_code
3. Get product from device
4. Get unit_weight
5. Calculate stock
6. Get previous reading
7. Apply validation & tolerance rules
8. Determine:

    * validation_status
    * is_anomaly
    * noise_level
9. Insert sensor_reading
10. Update stock_snapshot
11. Return response

---

# 📊 Stock Calculation

```
estimated_stock = floor(filtered_weight / unit_weight)
```

---

# 🧪 Validation Rules

## Basic Validation

* filtered_weight >= 0
* raw_weight >= 0
* device exists
* product exists
* unit_weight > 0

---

## Tolerance Rules

Define config:

```
MAX_WEIGHT_JUMP_PERCENT = 30
```

---

## Compare with Previous Reading

```
diff = current_weight - previous_weight
diffPercent = abs(diff) / previous_weight * 100
```

---

## Validation Logic

| Condition            | Result     |
| -------------------- | ---------- |
| weight < 0           | INVALID    |
| first data           | VALID      |
| diff > threshold     | SUSPICIOUS |
| raw vs filtered jauh | UNSTABLE   |
| normal               | VALID      |

---

# 📌 Validation Status

```
VALID
UNSTABLE
SUSPICIOUS
INVALID
```

---

# 📊 Noise Level

```
noise_level = abs(raw_weight - filtered_weight)
```

---

# 🗃️ Database Behavior

## sensor_readings

Semua data disimpan:

* VALID
* UNSTABLE
* SUSPICIOUS
* INVALID

Tujuan:

* histori
* debugging
* ML training

---

## stock_snapshots

Hanya update jika:

```
VALID → update
UNSTABLE → optional
SUSPICIOUS → skip
INVALID → skip
```

---

# 🔄 Flow Data

```
IoT
 ↓
sensor_readings (history)
 ↓
stock_snapshots (current state)
 ↓
client / dashboard
```

---

# 🧱 Domain Layer

```
SensorReading.kt
StockSnapshot.kt
```

---

# 📦 Repository Interface

```
SensorReadingRepository
StockSnapshotRepository
DeviceRepository
ProductRepository
```

---

# ⚙️ UseCase

```
ProcessSensorReadingUseCase.kt
```

---

# 🌐 Presentation Layer

```
SensorController.kt
SensorRoutes.kt
```

---

# 📥 DTO

```
CreateSensorReadingRequest.kt
CreateSensorReadingResponse.kt
```

---

# 🗄️ Data Layer

```
SensorReadingRepositoryImpl.kt
StockSnapshotRepositoryImpl.kt
```

---

# 🧪 Transaction Rule

Insert dan update harus dalam 1 transaction:

```
INSERT sensor_readings
UPDATE stock_snapshots
```

---

# 🧠 Business Rules

* Device harus terdaftar
* Device harus punya product
* Unit weight digunakan untuk konversi
* Semua data disimpan sebagai history
* Snapshot hanya representasi kondisi valid terbaru
* Validation rules harus configurable

---

# 🚀 MVP Version (Simplified)

Gunakan rule ini untuk awal:

* Validasi weight >= 0
* Validasi device exists
* Hitung stock
* Compare previous reading
* Jika diff > 30% → SUSPICIOUS
* Simpan semua data
* Snapshot update hanya VALID

---

# 🔥 Key Insight

```
sensor_readings = history
stock_snapshots = current state
```

---

# 🚀 Next Step After This

* Monitoring API
* Chart (history)
* ML prediction integration
* Rate limiting IoT
* Alert system (low stock)

---

# 🎯 Final Goal

Backend mampu:

* menerima data real-time
* memfilter noise
* mendeteksi anomaly
* menyimpan histori
* menyediakan data cepat untuk UI
* siap untuk ML

---
