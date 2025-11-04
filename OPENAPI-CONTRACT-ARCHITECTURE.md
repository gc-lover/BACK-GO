# OpenAPI как единственный источник истины

## 🎯 Философия

**OpenAPI спецификация = единственный источник истины для REST API контрактов**

Все аннотации Spring MVC (`@RequestMapping`, `@RequestParam`, `@PathVariable`, `@Valid`) определены в сгенерированных интерфейсах из OpenAPI спецификации.

## ✅ Архитектура

### 1. OpenAPI спецификация → API интерфейсы со Spring MVC аннотациями

**Входной файл:**
```yaml
# API-SWAGGER/api/v1/auth/character-creation.yaml
paths:
  /factions:
    get:
      operationId: getFactions
      parameters:
        - name: origin
          in: query
          required: false
          schema:
            type: string
      responses:
        '200':
          description: Список фракций
```

**Сгенерированный интерфейс:**
```java
// src/main/java/com/necpgame/backjava/api/FactionsApi.java
@Validated
@Tag(name = "Reference Data", description = "the Reference Data API")
public interface FactionsApi {
    
    @Operation(operationId = "getFactions", summary = "Список доступных фракций")
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/factions",
        produces = { "application/json" }
    )
    default ResponseEntity<GetFactions200Response> getFactions(
        @Parameter(name = "origin", in = ParameterIn.QUERY) 
        @Valid @RequestParam(value = "origin", required = false) 
        @Nullable String origin
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
```

### 2. Controller реализует API интерфейс

**Реализация:**
```java
// src/main/java/com/necpgame/backjava/controller/FactionsController.java
@RestController
@RequiredArgsConstructor
public class FactionsController implements FactionsApi {
    
    private final FactionsService factionsService;
    
    @Override
    public ResponseEntity<GetFactions200Response> getFactions(String origin) {
        // Все аннотации унаследованы из FactionsApi
        log.info("GET /factions?origin={}", origin);
        GetFactions200Response response = factionsService.getFactions(origin);
        return ResponseEntity.ok(response);
    }
}
```

## 🔄 Что генерируется автоматически

### 1. **API Interfaces** (со Spring MVC аннотациями)
- **Путь**: `src/main/java/com/necpgame/backjava/api/`
- **Примеры**: `AuthApi.java`, `CharactersApi.java`, `FactionsApi.java`
- **Содержит**: 
  - `@RequestMapping` - маршруты endpoints
  - `@RequestParam` - параметры запроса
  - `@PathVariable` - параметры пути
  - `@RequestBody` - тело запроса
  - `@Valid` - валидация
  - OpenAPI документацию (`@Operation`, `@ApiResponse`)

### 2. **DTOs** (модели данных)
- **Путь**: `src/main/java/com/necpgame/backjava/model/`
- **Примеры**: `CreateCharacterRequest.java`, `GameCharacter.java`
- **Содержит**: Jakarta Bean Validation аннотации

### 3. **Service Interfaces** (бизнес-логика контракты)
- **Путь**: `src/main/java/com/necpgame/backjava/service/`
- **Примеры**: `AuthService.java`, `CharactersService.java`
- **Содержит**: Чистые Java интерфейсы без Spring аннотаций
- **Генерируются через кастомный шаблон**: `templates/api.mustache`

## ✍️ Что создаётся вручную

### 1. **Controllers** (REST контроллеры)
```java
@RestController
@RequiredArgsConstructor
public class FactionsController implements FactionsApi {
    private final FactionsService service;
    
    @Override // Все аннотации из FactionsApi
    public ResponseEntity<...> getFactions(...) {
        return ResponseEntity.ok(service.getFactions(...));
    }
}
```

### 2. **ServiceImpl** (реализация бизнес-логики)
```java
@Service
@RequiredArgsConstructor
public class FactionsServiceImpl implements FactionsService {
    private final FactionRepository repository;
    
    @Override
    public GetFactions200Response getFactions(String origin) {
        // Бизнес-логика
    }
}
```

### 3. **Entities, Repositories, Mappers** и т.д.

## 🚀 Генерация контрактов

### Команда:
```powershell
cd BACK-GO
.\scripts\generate-openapi-layers.ps1 -ApiSpec ../API-SWAGGER/api/v1/auth/character-creation.yaml
```

### Что происходит:

1. **API Interfaces + DTOs** (стандартный Spring генератор):
   ```
   npx @openapitools/openapi-generator-cli generate
     -i ../API-SWAGGER/api/v1/auth/character-creation.yaml
     -g spring
     -p interfaceOnly=true,delegatePattern=false,useSpringBoot3=true
     → src/main/java/com/necpgame/backjava/api/
     → src/main/java/com/necpgame/backjava/model/
   ```

2. **Service Interfaces** (кастомный шаблон):
   ```
   npx @openapitools/openapi-generator-cli generate
     -i ../API-SWAGGER/api/v1/auth/character-creation.yaml
     -g spring
     -t templates
     --api-name-suffix Service
     -p interfaceOnly=true
     → src/main/java/com/necpgame/backjava/service/
   ```

## ✅ Преимущества

### 1. **OpenAPI = единственный источник правды**
- ✅ Все маршруты определены в OpenAPI
- ✅ Все параметры определены в OpenAPI
- ✅ Все валидации определены в OpenAPI
- ✅ Вся документация в одном месте

### 2. **Нет дублирования аннотаций**
- ✅ Controller НЕ дублирует `@RequestMapping`
- ✅ Controller НЕ дублирует `@RequestParam`
- ✅ Controller НЕ дублирует `@PathVariable`
- ✅ Все аннотации наследуются из API интерфейса

### 3. **Type Safety**
- ✅ Компилятор проверяет соответствие контроллера и API
- ✅ IDE автодополняет методы из интерфейса
- ✅ Невозможно забыть реализовать endpoint

### 4. **Автоматическая синхронизация**
- ✅ Изменения в OpenAPI → автоматически попадают в API интерфейс
- ✅ Компилятор сразу покажет несоответствия в контроллерах
- ✅ Нет рассинхронизации между документацией и кодом

## 🔄 Workflow разработки

### 1. Изменяем OpenAPI спецификацию
```yaml
# API-SWAGGER/api/v1/auth/character-creation.yaml
paths:
  /factions:
    get:
      operationId: getFactions
      parameters:
        - name: origin
          in: query
          schema:
            type: string
```

### 2. Генерируем контракты
```powershell
cd BACK-GO
.\scripts\generate-openapi-layers.ps1 -ApiSpec ../API-SWAGGER/api/v1/auth/character-creation.yaml
```

### 3. Компилятор показывает ошибки в контроллерах
```java
// Если добавили параметр в OpenAPI, компилятор покажет ошибку:
// "Method does not override or implement a method from a supertype"
```

### 4. Исправляем контроллеры
```java
@Override
public ResponseEntity<...> getFactions(String origin) { // Добавили параметр
    ...
}
```

### 5. Всё работает!
- ✅ OpenAPI обновлен
- ✅ API интерфейс обновлен
- ✅ Controller обновлен
- ✅ Документация актуальна

## 📁 Структура файлов

```
BACK-GO/
├── API-SWAGGER/api/v1/           ← OpenAPI спецификации (ИСТОЧНИК ИСТИНЫ)
│   └── auth/
│       └── character-creation.yaml
│
├── scripts/
│   └── generate-openapi-layers.ps1  ← Скрипт генерации
│
├── templates/
│   ├── api.mustache              ← Кастомный шаблон для Service интерфейсов
│   └── STRUCTURE.md
│
└── src/main/java/com/necpgame/backjava/
    ├── api/                      ← Сгенерированные API интерфейсы (со Spring MVC)
    │   ├── AuthApi.java          ← @RequestMapping, @RequestParam, @Valid
    │   ├── CharactersApi.java
    │   └── FactionsApi.java
    │
    ├── model/                    ← Сгенерированные DTOs
    │   ├── CreateCharacterRequest.java
    │   └── GameCharacter.java
    │
    ├── service/                  ← Сгенерированные Service интерфейсы
    │   ├── AuthService.java      ← Чистые Java интерфейсы
    │   └── CharactersService.java
    │
    ├── controller/               ← ВРУЧНУЮ: Controllers (implements API)
    │   ├── AuthController.java   ← implements AuthApi
    │   └── FactionsController.java ← implements FactionsApi
    │
    ├── service/impl/             ← ВРУЧНУЮ: ServiceImpl
    │   └── AuthServiceImpl.java  ← implements AuthService
    │
    ├── entity/                   ← ВРУЧНУЮ: JPA Entities
    ├── repository/               ← ВРУЧНУЮ: Spring Data Repositories
    └── mapper/                   ← ВРУЧНУЮ: MapStruct Mappers
```

## 📝 Примеры

### До (без OpenAPI контрактов):
```java
@RestController
@RequestMapping("/factions")
public class FactionsController {
    
    @GetMapping  // ❌ Дублирование с OpenAPI
    public ResponseEntity<...> getFactions(
        @RequestParam(required = false) String origin  // ❌ Дублирование
    ) {
        ...
    }
}
```

### После (OpenAPI как источник истины):
```java
@RestController
public class FactionsController implements FactionsApi {
    
    @Override  // ✅ Все аннотации из FactionsApi (OpenAPI)
    public ResponseEntity<...> getFactions(String origin) {
        ...
    }
}
```

## 🎉 Результат

- ✅ **OpenAPI = единственный источник истины**
- ✅ **Нет дублирования аннотаций**
- ✅ **Type Safety на уровне компиляции**
- ✅ **Автоматическая синхронизация документации и кода**
- ✅ **Быстрая разработка - меньше бойлерплейта**

---

**Дата:** 2025-01-27  
**Автор:** Backend Agent (AI)  
**Версия:** 2.0 (OpenAPI First Architecture)

