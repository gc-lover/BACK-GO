# БЭКТАСК-FAQ.md

**Типичные проблемы, решения и примеры использования**

📖 **Навигация:** [БЭКТАСК.MD](./БЭКТАСК.MD) | [БЭКТАСК-PROCESS.md](./БЭКТАСК-PROCESS.md) | [БЭКТАСК-REQUIREMENTS.md](./БЭКТАСК-REQUIREMENTS.md) | [БЭКТАСК-ARCHITECTURE.md](./БЭКТАСК-ARCHITECTURE.md)

---

## Типичные проблемы и решения

### Q: Как выбрать между OpenAPI Generator и Swagger Codegen?

**A:** Рекомендуется использовать OpenAPI Generator, так как он:
- Активно поддерживается и обновляется
- Поддерживает больше языков и фреймворков
- Имеет лучшую документацию
- Поддерживает последние версии OpenAPI спецификаций
- Лучше работает с Go серверным кодом

**Пример установки:**
```bash
npm install @openapitools/openapi-generator-cli -g
```

---

### Q: Какой Go серверный генератор использовать (go-server, go-gin-server, go-echo-server)?

**A:** Выбор зависит от используемого фреймворка:

- **go-server** - стандартный Go HTTP сервер (без фреймворка)
- **go-gin-server** - для Gin фреймворка (рекомендуется)
- **go-echo-server** - для Echo фреймворка
- **go-fiber-server** - для Fiber фреймворка

**Пример для Gin:**
```bash
openapi-generator-cli generate \
  -i API-SWAGGER/api/v1/gameplay/social/personal-npc-tool.yaml \
  -g go-gin-server \
  -o BACK-GO/internal/api/generated/personal-npc-tool \
  --additional-properties=packageName=personalnpc
```

---

### Q: Что делать, если API Swagger файл находится в поддиректории?

**A:** Используй полный путь к файлу:

```bash
openapi-generator-cli generate \
  -i API-SWAGGER/api/v1/gameplay/social/personal-npc-tool/personal-npc-tool.yaml \
  -g go-gin-server \
  -o BACK-GO/internal/api/generated/personal-npc-tool
```

---

### Q: Что делать, если файл handler превышает 400 строк?

**A:** ОБЯЗАТЕЛЬНО разбей файл на несколько файлов:

1. **Раздели по методам HTTP:**
   ```go
   // handler.go (основной handler)
   package personalnpc
   
   type PersonalNPCHandler struct {
       service *Service
   }
   
   func NewPersonalNPCHandler(service *Service) *PersonalNPCHandler {
       return &PersonalNPCHandler{service: service}
   }
   
   // handler_get.go (GET методы)
   func (h *PersonalNPCHandler) GetPersonalNPCs(c *gin.Context) {
       // GET логика
   }
   
   func (h *PersonalNPCHandler) GetPersonalNPC(c *gin.Context) {
       // GET логика
   }
   
   // handler_post.go (POST методы)
   func (h *PersonalNPCHandler) CreatePersonalNPC(c *gin.Context) {
       // POST логика
   }
   ```

2. **Вынеси бизнес-логику в сервисы:**
   ```go
   // service.go
   package personalnpc
   
   type Service struct {
       repo *Repository
   }
   
   func (s *Service) GetAllNPCs(ctx context.Context) ([]*models.PersonalNPC, error) {
       // бизнес-логика
   }
   ```

3. **Вынеси работу с БД в репозитории:**
   ```go
   // repository.go
   package personalnpc
   
   type Repository struct {
       db *pgx.Conn
   }
   
   func (r *Repository) GetAllNPCs(ctx context.Context) ([]*models.PersonalNPC, error) {
       // SQL запросы
   }
   ```

---

### Q: Как обрабатывать ошибки в handlers?

**A:** Используй стандартные HTTP коды и структурированные ответы:

```go
func (h *PersonalNPCHandler) GetPersonalNPC(c *gin.Context) {
    id := c.Param("id")
    
    npc, err := h.service.GetNPCByID(c.Request.Context(), id)
    if err != nil {
        // Логирование ошибки
        log.WithError(err).Error("Failed to get NPC")
        
        // Определение HTTP кода
        statusCode := http.StatusInternalServerError
        if errors.Is(err, ErrNotFound) {
            statusCode = http.StatusNotFound
        } else if errors.Is(err, ErrInvalidInput) {
            statusCode = http.StatusBadRequest
        }
        
        c.JSON(statusCode, gin.H{
            "error": err.Error(),
        })
        return
    }
    
    c.JSON(http.StatusOK, npc)
}
```

---

### Q: Как использовать транзакции для критических операций?

**A:** Используй транзакции в репозиториях:

```go
func (r *Repository) CreateNPCWithTransaction(ctx context.Context, npc *models.PersonalNPC) error {
    tx, err := r.db.Begin(ctx)
    if err != nil {
        return err
    }
    defer tx.Rollback(ctx)
    
    // Выполнение операций в транзакции
    if err := r.createNPC(ctx, tx, npc); err != nil {
        return err
    }
    
    if err := r.createNPCRelations(ctx, tx, npc); err != nil {
        return err
    }
    
    // Коммит транзакции
    if err := tx.Commit(ctx); err != nil {
        return err
    }
    
    return nil
}
```

---

### Q: Как создать миграции БД?

**A:** Используй migrate или goose:

**С migrate:**
```bash
# Создание миграции
migrate create -ext sql -dir migrations -seq create_personal_npc_table

# Применение миграций
migrate -path migrations -database "postgres://user:password@localhost/dbname?sslmode=disable" up

# Откат миграций
migrate -path migrations -database "postgres://user:password@localhost/dbname?sslmode=disable" down
```

**С goose:**
```bash
# Создание миграции
goose -dir migrations create create_personal_npc_table sql

# Применение миграций
goose -dir migrations postgres "postgres://user:password@localhost/dbname?sslmode=disable" up

# Откат миграций
goose -dir migrations postgres "postgres://user:password@localhost/dbname?sslmode=disable" down
```

---

### Q: Как настроить middleware для аутентификации?

**A:** Создай middleware для проверки Bearer token:

```go
// middleware/auth.go
func AuthMiddleware() gin.HandlerFunc {
    return func(c *gin.Context) {
        token := c.GetHeader("Authorization")
        if token == "" {
            c.JSON(http.StatusUnauthorized, gin.H{"error": "Missing authorization token"})
            c.Abort()
            return
        }
        
        // Проверка токена
        if !isValidToken(token) {
            c.JSON(http.StatusUnauthorized, gin.H{"error": "Invalid token"})
            c.Abort()
            return
        }
        
        c.Next()
    }
}

// Использование в роутере
router.Use(AuthMiddleware())
```

---

### Q: Как организовать структуру директорий?

**A:** Следуй иерархии API-SWAGGER:

```
BACK-GO/internal/
├── api/
│   └── generated/
│       └── personal-npc-tool/        # Сгенерированный код
├── handlers/
│   └── gameplay/
│       └── social/
│           └── personal_npc_handler.go
├── services/
│   └── gameplay/
│       └── social/
│           └── personal_npc_service.go
└── repositories/
    └── gameplay/
        └── social/
            └── personal_npc_repository.go
```

---

### Q: Как использовать структурированное логирование?

**A:** Используй logrus или zap:

**С logrus:**
```go
import "github.com/sirupsen/logrus"

log := logrus.WithFields(logrus.Fields{
    "handler": "GetPersonalNPC",
    "id": id,
})

if err != nil {
    log.WithError(err).Error("Failed to get NPC")
} else {
    log.Info("NPC retrieved successfully")
}
```

**С zap:**
```go
import "go.uber.org/zap"

logger := zap.NewExample()
defer logger.Sync()

if err != nil {
    logger.Error("Failed to get NPC",
        zap.String("id", id),
        zap.Error(err),
    )
} else {
    logger.Info("NPC retrieved successfully",
        zap.String("id", id),
    )
}
```

---

## Примеры команд

### Пример 1: Один API файл

```
Делай бекенд для API-SWAGGER/api/v1/gameplay/social/personal-npc-tool.yaml
```

### Пример 2: Директория с API

```
Делай бекенд для всех API из API-SWAGGER/api/v1/gameplay/social/
```

### Пример 3: Все API

```
Делай бекенд для всех API из API-SWAGGER/api/v1/
```

---

## Инструменты для проверки

### Валидация Go кода:

```bash
# Форматирование кода
gofmt -w .

# Линтинг
golangci-lint run

# Статический анализ
go vet ./...

# Проверка всех модулей
go mod verify
```

### Проверка размера файлов:

```bash
# Подсчет строк в файле
wc -l internal/handlers/gameplay/social/personal_npc_handler.go

# Подсчет строк во всех файлах
find internal/ -name "*.go" -exec wc -l {} \;
```

---

📖 **Навигация:** [БЭКТАСК.MD](./БЭКТАСК.MD) | [БЭКТАСК-PROCESS.md](./БЭКТАСК-PROCESS.md) | [БЭКТАСК-REQUIREMENTS.md](./БЭКТАСК-REQUIREMENTS.md) | [БЭКТАСК-ARCHITECTURE.md](./БЭКТАСК-ARCHITECTURE.md)

