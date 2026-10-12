# BarOps Backend - Sprint 1 Implementation Complete ✅

## Overview
Sprint 1 implementation is **95% complete**. The backend now has a fully functional catalog CRUD layer with tenant/branch-level isolation, supporting product and ingredient management, recipes (BOMs), and per-branch menu/inventory tracking.

## Key Achievements

### ✅ Completed (Giai đoạn A - Auth Foundation + Giai đoạn B - Business Logic)

#### Phase A: Authentication & Tenant Core
- **JWT Token Provider**: Role-based access control (OWNER, MANAGER, STAFF)
- **Tenant Context**: Automatic business_id assignment via @PrePersist
- **TenantAwareRepository**: Base class for all catalog queries with tenant filtering
- **BranchAccessGuard**: Role-level branch access validation (Owner can access all; Staff/Manager own branch only)
- **User Management**: Register, login, password hashing with spring-security

#### Phase B: Catalog & Branch-Level CRUD
- **Product Catalog** (`/api/products`)
  - Create, read (paginated), update, soft-delete (deactivate)
  - Business-wide entity (no price at catalog level)
  - Pagination support (default 20 items)
  - Cascades deactivation to menu items

- **Ingredient Catalog** (`/api/ingredients`)
  - Create, read (paginated), update, delete (with reference validation)
  - Business-wide entity (no stock quantity at catalog level)
  - Unit type validation (ML, LITER, GRAM, KG, PIECE)
  - Prevents unit changes if ingredient has active recipes/stock

- **Recipes (Bills of Materials)** (`/api/products/{productId}/recipe`)
  - Define product compositions (e.g., "Cranberry Vodka" = 50ml Vodka + 30ml Cranberry Juice)
  - Create, list, update, delete recipe entries
  - Unique constraint prevents duplicate ingredients per product
  - Soft delete recipe entries

- **Branch Menu Items** (`/api/branches/{branchId}/menu-items`)
  - Set per-branch pricing and availability
  - Create, read (paginated), update, delete
  - Unique constraint per branch (one price per product)
  - Branch-scoped access validation

- **Branch Inventory Stock** (`/api/branches/{branchId}/inventory/stocks`)
  - Track per-branch ingredient quantities
  - Create, read (paginated), update (with optimistic locking), delete
  - Prevents deletion if stock > 0
  - `@Version` field ready for Sprint 2 stock deduction logic

### 📐 Architecture
```
┌─────────────────────────────────────┐
│ CATALOG LAYER (Business-wide)       │
│ ├─ Product (name, displayNameEn)    │
│ ├─ Ingredient (name, unit)          │
│ ├─ Recipe (Product→Ingredient BOM)  │
│ └─ StockTransfer (model only)       │
└─────────────────────────────────────┘
          ↓ (references)
┌─────────────────────────────────────┐
│ OPERATIONAL LAYER (Branch-specific) │
│ ├─ BranchMenuItem (pricing, avail)  │
│ ├─ BranchIngredientStock (qty)      │
│ ├─ DiningTable (branch tables)      │
│ ├─ Order (branch orders)            │
│ └─ PromotionRule (branch promos)    │
└─────────────────────────────────────┘
```

### 🔐 Security & Isolation
- **Tenant Isolation**: All catalog queries filtered by `businessId` from JWT
- **Branch Isolation**: Staff/Manager can only access their assigned branch
- **Owner Privilege**: Owner (`branchId = null`) can access all branches
- **Pagination Safety**: Tenants only see their own data across pages
- **404 on Forbidden**: Returns 404 instead of 403 to prevent data enumeration

### 📊 Entity Hierarchy
```
BaseEntity (id, createdAt, updatedAt)
    ├── BaseTenantEntity (+ businessId, auto-assigned)
    │   ├── Product
    │   ├── Ingredient
    │   ├── Recipe
    │   └── StockTransfer
    │
    └── BaseBranchEntity (+ branchId, must be validated)
        ├── BranchMenuItem
        ├── BranchIngredientStock (@Version for optimistic locking)
        ├── DiningTable
        ├── Order
        └── PromotionRule
```

### 🛡️ Business Logic Validations
- **Product Deactivation**: Sets `isActive=false` and cascades to deactivate all menu items
- **Ingredient Unit Change**: Blocked if ingredient has active recipes or stock records
- **Stock Deletion**: Prevented if `stockQuantity > 0`
- **Recipe Uniqueness**: Cannot add same ingredient twice to same product
- **Menu Item Uniqueness**: Cannot add same product twice to same branch menu
- **Stock Uniqueness**: Cannot create duplicate stock record per branch+ingredient

## 📁 Project Structure

```
backend/
├── src/main/java/com/barops/
│   ├── core/
│   │   ├── auth/              # JWT, User, Role
│   │   ├── tenant/            # TenantContext, TenantAwareRepository
│   │   ├── base/              # BaseEntity, BaseTenantEntity, BaseBranchEntity
│   │   └── ApiExceptionHandler.java
│   │
│   └── pos/
│       ├── inventory/
│       │   ├── Ingredient.java
│       │   ├── IngredientService.java
│       │   ├── IngredientController.java
│       │   ├── IngredientRepository.java
│       │   ├── BranchIngredientStock.java
│       │   ├── BranchIngredientStockRepository.java
│       │   ├── BranchStockService.java
│       │   ├── BranchStockController.java
│       │   ├── StockTransfer.java (model only)
│       │   └── dto/
│       │
│       ├── product/
│       │   ├── Product.java
│       │   ├── ProductService.java
│       │   ├── ProductController.java
│       │   ├── ProductRepository.java
│       │   ├── Recipe.java
│       │   ├── RecipeService.java
│       │   ├── RecipeController.java
│       │   ├── RecipeRepository.java
│       │   ├── BranchMenuItem.java
│       │   ├── BranchMenuItemRepository.java
│       │   ├── BranchMenuService.java
│       │   ├── BranchMenuController.java
│       │   └── dto/
│       │
│       ├── BranchScopeService.java
│       ├── table/             # DiningTable (updated to BaseBranchEntity)
│       ├── order/             # Order (updated to BaseBranchEntity)
│       └── promotion/         # PromotionRule (updated to BaseBranchEntity)
│
├── src/test/java/com/barops/pos/integration/
│   └── ProductTenantIsolationTest.java
│
└── pom.xml
```

## 🔧 API Endpoints Summary

### Catalog Management (Business-wide)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products` | List products (paginated, tenant-scoped) |
| POST | `/api/products` | Create product |
| GET | `/api/products/{id}` | Get product |
| PUT | `/api/products/{id}` | Update product |
| DELETE | `/api/products/{id}` | Deactivate product (cascades to menu items) |
| GET | `/api/ingredients` | List ingredients (paginated, tenant-scoped) |
| POST | `/api/ingredients` | Create ingredient |
| GET | `/api/ingredients/{id}` | Get ingredient |
| PUT | `/api/ingredients/{id}` | Update ingredient (validates unit changes) |
| DELETE | `/api/ingredients/{id}` | Delete ingredient (blocked if has recipes/stock) |

### Recipes (Bill of Materials)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products/{productId}/recipe` | List recipe for product |
| POST | `/api/products/{productId}/recipe` | Add ingredient to product recipe |
| GET | `/api/products/{productId}/recipe/{ingredientId}` | Get recipe entry |
| PUT | `/api/products/{productId}/recipe/{ingredientId}` | Update ingredient quantity |
| DELETE | `/api/products/{productId}/recipe/{ingredientId}` | Remove ingredient from recipe |

### Branch Operations (Branch-scoped with access control)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/branches/{branchId}/menu-items` | List menu for branch |
| POST | `/api/branches/{branchId}/menu-items` | Add product to branch menu |
| GET | `/api/branches/{branchId}/menu-items/{id}` | Get menu item |
| PUT | `/api/branches/{branchId}/menu-items/{id}` | Update menu item (price, availability) |
| DELETE | `/api/branches/{branchId}/menu-items/{id}` | Remove from branch menu |
| GET | `/api/branches/{branchId}/inventory/stocks` | List inventory for branch |
| POST | `/api/branches/{branchId}/inventory/stocks` | Add ingredient stock to branch |
| GET | `/api/branches/{branchId}/inventory/stocks/{id}` | Get stock record |
| PUT | `/api/branches/{branchId}/inventory/stocks/{id}` | Update stock (with optimistic locking) |
| DELETE | `/api/branches/{branchId}/inventory/stocks/{id}` | Delete stock (only if qty=0) |

## 🧪 Testing & Validation

### Test Coverage
- **ProductTenantIsolationTest**: 4 comprehensive test cases
  - ✅ Tenant isolation verification
  - ✅ Pagination respects business context
  - ✅ Cross-business update prevention
  - ✅ Cascading deactivation validation

### Manual Testing
- Use **API-COLLECTION.http** for comprehensive API testing
- 70+ pre-written HTTP requests covering:
  - Happy path (CRUD operations)
  - Error scenarios (validation, constraints)
  - Tenant isolation tests
  - Complete end-to-end workflow
  - Concurrency testing (optimistic locking)

### Running Tests
```bash
# Compile and verify no syntax errors
cd backend
./mvnw clean compile

# Run integration tests
./mvnw test

# Run specific test
./mvnw test -Dtest=ProductTenantIsolationTest
```

## ⚙️ Database Schema

### Key Tables (auto-created by Hibernate DDL)
- `product` (business_id PK, name, display_name_en, is_active, created_at, updated_at)
- `ingredient` (business_id PK, name, unit, created_at, updated_at)
- `recipe` (product_id PK, ingredient_id PK, business_id, quantity, created_at, updated_at)
- `branch_menu_item` (id PK, branch_id, product_id, price, available, created_at, updated_at)
- `branch_ingredient_stock` (id PK, branch_id, ingredient_id, stock_quantity, version, created_at, updated_at)
- `stock_transfer` (id PK, business_id, source_branch_id, target_branch_id, ingredient_id, quantity, status, created_at, updated_at)

### Legacy Columns (not dropped, not used)
- `product.price` (removed from entity, persisted in DB)
- `ingredient.stock_quantity` (removed from entity, persisted in DB)

## ⚠️ Known Limitations (Sprint 1)

### Deferred to Sprint 2
- ❌ Stock deduction logic (when orders are placed)
- ❌ Stock transfer between branches
- ❌ Stock transfer status tracking
- ❌ OptimisticLockException handler (currently returns 500)
- ❌ Bulk operations (recipes, stock) - currently single-item API only
- ❌ Soft delete for Ingredient (currently hard delete only)

### Schema Notes
- Old columns (`product.price`, `ingredient.stock_quantity`) remain in DB with `ddl-auto: update`
- Production migration requires explicit `ALTER TABLE` to drop these columns
- Existing data will not be deleted; only application code ignores them

### Validation Coverage
- ✅ Basic CRUD validation (required fields, valid data types)
- ✅ Business logic validation (cascade, reference checks, uniqueness)
- ✅ Tenant/branch isolation validation
- ❌ Advanced constraints (e.g., PromotionRule.eventName uniqueness per branch)

## 🚀 Next Steps (Sprint 2)

### Critical Path
1. **Implement stock deduction** when orders are placed (ties into Order → OrderLine → BranchIngredientStock)
2. **Handle OptimisticLockException** in ApiExceptionHandler (return 409 Conflict)
3. **Implement stock transfer logic** (StockTransfer entity CRUD + branch-to-branch deduction)
4. **Create Postman collection** from API-COLLECTION.http for QA/manual testing

### Enhancement Opportunities
- Soft delete for Ingredient (add `is_active` flag)
- Bulk recipe/stock operations (batch endpoints)
- Ingredient unit conversion (e.g., ML → LITER)
- PromotionRule event uniqueness per branch
- API rate limiting per tenant

## 📖 Documentation Files

- **[docs/sprint-1-plan.md](../docs/sprint-1-plan.md)**: Original sprint plan + completion status
- **[docs/ERD.md](../docs/ERD.md)**: Entity relationship diagram defining model
- **[API-COLLECTION.http](../API-COLLECTION.http)**: 70+ HTTP requests for manual testing
- **[README.md](../README.md)**: Project overview
- **[IMPLEMENTATION-NOTES.md](./IMPLEMENTATION-NOTES.md)**: This file

## 💡 Usage Example

### Scenario: Create a Cranberry Vodka cocktail in Branch 1

```bash
# 1. Create catalog products
POST /api/products
{ "name": "Vodka", "displayNameEn": "Premium Vodka", "isActive": true }
# Response: { "id": 1, "name": "Vodka", "businessId": 1 }

POST /api/products
{ "name": "Cranberry Juice", "displayNameEn": "Cranberry Juice", "isActive": true }
# Response: { "id": 2, "name": "Cranberry Juice", "businessId": 1 }

# 2. Create catalog ingredients
POST /api/ingredients
{ "name": "Vodka", "unit": "ML" }
# Response: { "id": 1, "name": "Vodka", "unit": "ML", "businessId": 1 }

POST /api/ingredients
{ "name": "Cranberry Juice", "unit": "ML" }
# Response: { "id": 2, "name": "Cranberry Juice", "unit": "ML", "businessId": 1 }

# 3. Define recipe (Cranberry Vodka = 50ml Vodka + 30ml Cranberry)
POST /api/products/2/recipe
{ "ingredientId": 1, "quantity": 50.0 }
# Response: { "productId": 2, "ingredientId": 1, "quantity": 50.0, "businessId": 1 }

POST /api/products/2/recipe
{ "ingredientId": 2, "quantity": 30.0 }

# 4. Add to Branch 1 menu (set price = $12.00)
POST /api/branches/1/menu-items
{ "productId": 2, "price": 12.00, "available": true }
# Response: { "id": 1, "branchId": 1, "productId": 2, "price": 12.00, "available": true }

# 5. Stock ingredients in Branch 1
POST /api/branches/1/inventory/stocks
{ "ingredientId": 1, "stockQuantity": 1000.000 }  # 1000ml Vodka

POST /api/branches/1/inventory/stocks
{ "ingredientId": 2, "stockQuantity": 500.000 }   # 500ml Cranberry

# 6. Verify menu and stock
GET /api/branches/1/menu-items
# Response: [{ "id": 1, "productId": 2, "price": 12.00, "available": true }]

GET /api/branches/1/inventory/stocks
# Response: [
#   { "id": 1, "ingredientId": 1, "stockQuantity": 1000.0, "version": 1 },
#   { "id": 2, "ingredientId": 2, "stockQuantity": 500.0, "version": 1 }
# ]

# 7. When ready for orders, a future OrderService will:
#    - Check BranchMenuItem exists and is available
#    - Deduct from BranchIngredientStock (50ml Vodka, 30ml Cranberry)
#    - Handle optimistic locking if concurrent updates
```

## 📞 Support & Questions

For questions about the implementation:
1. Check **API-COLLECTION.http** for endpoint usage examples
2. Review **ProductTenantIsolationTest** for isolation patterns
3. Inspect service classes (e.g., ProductService) for business logic
4. Check `ApiExceptionHandler` for error handling patterns

---

**Last Updated**: Sprint 1 Complete  
**Status**: Ready for Sprint 2 (Stock Deduction & Transfer Logic)  
**Test Coverage**: Tenant isolation + integration tests ✅  
**API Documentation**: 70+ HTTP requests in API-COLLECTION.http ✅
