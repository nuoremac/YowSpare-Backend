@echo off
REM Script de creation de l'arborescence du projet Spring Boot

REM Packages Java
md src\main\java\yowyob\comops\api\config
md src\main\java\yowyob\comops\api\controller
md src\main\java\yowyob\comops\api\model
md src\main\java\yowyob\comops\api\repository
md src\main\java\yowyob\comops\api\service

REM Fichiers Java
type nul > src\main\java\yowyob\comops\api\ApiApplication.java
type nul > src\main\java\yowyob\comops\api\config\CorsConfig.java
type nul > src\main\java\yowyob\comops\api\config\DataInitializer.java
type nul > src\main\java\yowyob\comops\api\config\ScyllaDbConfig.java
type nul > src\main\java\yowyob\comops\api\controller\ClientController.java
type nul > src\main\java\yowyob\comops\api\controller\FiscalYearController.java
type nul > src\main\java\yowyob\comops\api\controller\GeneralOptionsController.java
type nul > src\main\java\yowyob\comops\api\controller\InvoiceController.java
type nul > src\main\java\yowyob\comops\api\controller\OrderController.java
type nul > src\main\java\yowyob\comops\api\controller\PersonnelController.java
type nul > src\main\java\yowyob\comops\api\controller\ProductController.java
type nul > src\main\java\yowyob\comops\api\controller\StockController.java
type nul > src\main\java\yowyob\comops\api\controller\SupplierController.java
type nul > src\main\java\yowyob\comops\api\model\Client.java
type nul > src\main\java\yowyob\comops\api\model\ClientSummary.java
type nul > src\main\java\yowyob\comops\api\model\DbJson.java
type nul > src\main\java\yowyob\comops\api\model\FiscalYear.java
type nul > src\main\java\yowyob\comops\api\model\GeneralOptions.java
type nul > src\main\java\yowyob\comops\api\model\Inventory.java
type nul > src\main\java\yowyob\comops\api\model\InventoryItem.java
type nul > src\main\java\yowyob\comops\api\model\Invoice.java
type nul > src\main\java\yowyob\comops\api\model\Order.java
type nul > src\main\java\yowyob\comops\api\model\OrderItem.java
type nul > src\main\java\yowyob\comops\api\model\Payment.java
type nul > src\main\java\yowyob\comops\api\model\PermissionSet.java
type nul > src\main\java\yowyob\comops\api\model\Product.java
type nul > src\main\java\yowyob\comops\api\model\ProductTransformation.java
type nul > src\main\java\yowyob\comops\api\model\ProductTransformationItem.java
type nul > src\main\java\yowyob\comops\api\model\Profile.java
type nul > src\main\java\yowyob\comops\api\model\StockMovement.java
type nul > src\main\java\yowyob\comops\api\model\StockMovementItem.java
type nul > src\main\java\yowyob\comops\api\model\Supplier.java
type nul > src\main\java\yowyob\comops\api\model\SystemAudit.java
type nul > src\main\java\yowyob\comops\api\model\User.java
type nul > src\main\java\yowyob\comops\api\model\Warehouse.java
type nul > src\main\java\yowyob\comops\api\model\WarehouseTransfer.java
type nul > src\main\java\yowyob\comops\api\model\WarehouseTransferItem.java
type nul > src\main\java\yowyob\comops\api\repository\ClientRepository.java
type nul > src\main\java\yowyob\comops\api\repository\FiscalYearRepository.java
type nul > src\main\java\yowyob\comops\api\repository\GeneralOptionsRepository.java
type nul > src\main\java\yowyob\comops\api\repository\InventoryRepository.java
type nul > src\main\java\yowyob\comops\api\repository\InvoiceRepository.java
type nul > src\main\java\yowyob\comops\api\repository\OrderRepository.java
type nul > src\main\java\yowyob\comops\api\repository\ProductRepository.java
type nul > src\main\java\yowyob\comops\api\repository\ProductTransformationRepository.java
type nul > src\main\java\yowyob\comops\api\repository\ProfileRepository.java
type nul > src\main\java\yowyob\comops\api\repository\StockMovementRepository.java
type nul > src\main\java\yowyob\comops\api\repository\SupplierRepository.java
type nul > src\main\java\yowyob\comops\api\repository\SystemAuditRepository.java
type nul > src\main\java\yowyob\comops\api\repository\UserRepository.java
type nul > src\main\java\yowyob\comops\api\repository\WarehouseRepository.java
type nul > src\main\java\yowyob\comops\api\repository\WarehouseTransferRepository.java
type nul > src\main\java\yowyob\comops\api\service\ApiService.java

echo Arborescence creee avec succes.