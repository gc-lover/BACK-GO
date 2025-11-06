# Критически важные рекомендации для Backend Agent (из опыта)

**Дата создания:** 2025-11-06  
**Последнее обновление:** 2025-11-06 22:42  
**Версия:** 1.0.0

**Назначение:** Best practices и решение типичных проблем при разработке backend

**Основной документ:** См. [БЭКТАСК.MD](./БЭКТАСК.MD)

---

## 1. ⚠️ Enum маппинг между DTO и Entity

**ПРОБЛЕМА:** OpenAPI DTO enum использует UPPERCASE (`MALE`, `MUSCULAR`), а Entity enum - lowercase (`male`, `muscular`).

### ❌ НЕПРАВИЛЬНО:

```java
// Это вызовет IllegalArgumentException!
character.setGender(CharacterEntity.Gender.valueOf(request.getGender().name()));
// name() возвращает "MALE", а enum ожидает "male"
```

### ✅ ПРАВИЛЬНО:

```java
// Используй getValue() для получения lowercase значения
character.setGender(CharacterEntity.Gender.valueOf(request.getGender().getValue()));
// getValue() возвращает "male"
```

### ✅ В MapStruct:

```java
@Named("enumToBodyType")
default CharacterAppearanceEntity.BodyType enumToBodyType(GameCharacterAppearance.BodyTypeEnum bodyType) {
    // ✅ getValue() вместо name()
    return bodyType != null ? CharacterAppearanceEntity.BodyType.valueOf(bodyType.getValue()) : null;
}
```

---

## 2. 🔍 GlobalExceptionHandler ОБЯЗАТЕЛЬНО должен возвращать details

**ПРОБЛЕМА:** Без детальных ошибок невозможно дебажить API.

### ✅ ОБЯЗАТЕЛЬНО:

- Заполняй поле `details` для ВСЕХ исключений
- Добавь обработчики для `MethodArgumentNotValidException` (валидация)
- Добавь обработчики для `HttpMessageNotReadableException` (JSON парсинг)
- Для непредвиденных исключений добавляй stack trace в details

### Пример правильного GlobalExceptionHandler:

```java
@ExceptionHandler(Exception.class)
public ResponseEntity<Error> handleUnexpectedException(Exception ex) {
    log.error("Unexpected exception: {}", ex.getMessage(), ex);
    
    List<String> details = new ArrayList<>();
    details.add("Exception: " + ex.getClass().getSimpleName());
    details.add("Message: " + ex.getMessage());
    if (ex.getCause() != null) {
        details.add("Cause: " + ex.getCause().getMessage());
    }
    // Stack trace для отладки
    StackTraceElement[] stackTrace = ex.getStackTrace();
    if (stackTrace.length > 0) {
        details.add("At: " + stackTrace[0].toString());
    }
    
    errorDetails.setDetails(details);
    // ...
}
```

---

## 3. 📝 Правильные имена полей в JSON (snake_case)

**ПРОБЛЕМА:** OpenAPI использует snake_case, PowerShell может отправлять camelCase.

### ✅ ВСЕГДА используй snake_case:

```json
{
  "password_confirm": "Pass123!",
  "terms_accepted": true,
  "city_id": "550e8400-...",
  "skin_color": "tan",
  "body_type": "muscular"
}
```

### ❌ НЕ используй camelCase:

```json
{
  "passwordConfirm": "Pass123!",  // ❌ Ошибка!
  "termsAccepted": true,           // ❌ Ошибка!
  "skinTone": "tan"                // ❌ Ошибка!
}
```

---

## 4. 🧪 Тестирование с правильными enum значениями

**DTO Enum → lowercase в JSON:**

```json
{
  "gender": "male",           // ✅ не "MALE"
  "body_type": "muscular",    // ✅ не "MUSCULAR"
  "origin": "street_kid"      // ✅ не "STREET_KID"
}
```

**Проверяй допустимые значения в OpenAPI спецификации ПЕРЕД тестированием!**

---

## 5. 🔐 SecurityUtil.getCurrentAccountId() - временная заглушка

**ВНИМАНИЕ:** Текущая реализация возвращает фиксированный UUID для тестирования:

```java
public static UUID getCurrentAccountId() {
    // Временная заглушка для тестирования
    return UUID.fromString("00000000-0000-0000-0000-000000000001");
}
```

**Для тестирования:**
1. Создай тестовый аккаунт с этим UUID в БД
2. ИЛИ измени SecurityUtil для реальной JWT аутентификации

**TODO:** Реализовать извлечение accountId из JWT токена в production

---

## 6. 📊 Проверка seed данных перед тестированием

**ОБЯЗАТЕЛЬНО проверь наличие справочных данных:**

```sql
SELECT COUNT(*) FROM factions;    -- Должно быть > 0
SELECT COUNT(*) FROM cities;      -- Должно быть > 0
SELECT COUNT(*) FROM character_classes;  -- Должно быть > 0
SELECT id FROM cities LIMIT 1;    -- Получи UUID для тестирования
```

**Используй реальные UUID из БД в тестах!**

---

## 7. 🔄 Перезапуск после изменений

**После изменений в:**
- Service/Controller/Mapper - ОБЯЗАТЕЛЬНО перезапусти приложение
- GlobalExceptionHandler - ОБЯЗАТЕЛЬНО перезапусти
- Entity - выполни `mvn clean compile`, затем перезапусти

**Команда быстрого перезапуска:**

```powershell
# Останови старый процесс
taskkill /F /PID <PID>

# Перезапусти
mvn spring-boot:run
```

---

**Версия документа:** 1.0.0  
**Дата последнего обновления:** 2025-11-06 22:42

