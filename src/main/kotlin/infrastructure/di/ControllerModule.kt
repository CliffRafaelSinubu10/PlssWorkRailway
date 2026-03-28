package infrastructure.di

import infrastructure.security.JwtService
import presentation.controller.AuthController
import presentation.controller.ProductController
import presentation.controller.IOTDeviceController
import presentation.controller.SensorController

class ControllerModule(
    private val useCaseModule: UseCaseModule,
    private val jwtService: JwtService
) {
    val authController: AuthController by lazy { 
        AuthController(
            registerUseCase = useCaseModule.registerUseCase,
            loginUseCase = useCaseModule.loginUseCase,
            jwtService = jwtService
        ) 
    }

    val productController: ProductController by lazy {
        ProductController(
            createProductUseCase = useCaseModule.createProductUseCase,
            getProductsUseCase = useCaseModule.getProductsUseCase,
            getProductByIdUseCase = useCaseModule.getProductByIdUseCase,
            updateProductUseCase = useCaseModule.updateProductUseCase,
            deleteProductUseCase = useCaseModule.deleteProductUseCase
        )
    }

    val iotDeviceController: IOTDeviceController by lazy {
        IOTDeviceController(
            registerDeviceUseCase = useCaseModule.registerDeviceUseCase,
            getDevicesUseCase = useCaseModule.getDevicesUseCase,
            getDeviceByIdUseCase = useCaseModule.getDeviceByIdUseCase,
            updateDeviceUseCase = useCaseModule.updateDeviceUseCase,
            deleteDeviceUseCase = useCaseModule.deleteDeviceUseCase,
            updateDeviceLastSeenUseCase = useCaseModule.updateDeviceLastSeenUseCase // <-- Tambahin ini
        )
    }

    val sensorController: SensorController by lazy {
        SensorController(useCaseModule.processSensorDataUseCase)
    }
}
