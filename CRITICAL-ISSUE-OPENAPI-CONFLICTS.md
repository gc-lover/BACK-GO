# 🚨 КРИТИЧЕСКАЯ ПРОБЛЕМА: Конфликты OpenAPI спецификаций

**Дата:** 2025-11-07 01:05  
**Статус:** ⚠️ **БЛОКЕР для дальнейшей генерации**

---

## 🐛 ПРОБЛЕМА

### Конфликтующие OpenAPI specs используют ОДИНАКОВЫЕ имена схем!

**Пример:**

**1. character-creation.yaml** определяет `GameCharacter`:
```yaml
GameCharacter:
  properties:
    accountId: UUID
    class: enum [SOLO, NETRUNNER, TECHIE, ...]
    origin: enum [NOMAD, STREET_KID, CORPO]
    gender: enum [MALE, FEMALE, NON_BINARY]
    # ... +20 полей
```

**2. player-management.yaml** ПЕРЕОПРЕДЕЛЯЕТ `GameCharacter`:
```yaml
GameCharacter:
  properties:
    characterId: string
    playerId: string
    classId: string  # Не enum!
    level: integer
    # ДРУГИЕ поля!
```

### Результат:
- OpenAPI Generator **ПЕРЕЗАПИСЫВАЕТ** старый `GameCharacter.java`
- Старые компоненты (CharacterMapperMS) **НЕ КОМПИЛИРУЮТСЯ**
- Enum'ы исчезают → MapStruct падает

---

## 💥 ПОСЛЕДСТВИЯ

### Что сломалось:

1. **CharacterMapperMS.java:**
   ```java
   // ОШИБКА: PropertyClassEnum не существует!
   GameCharacter.PropertyClassEnum.fromValue(classCode)
   ```

2. **CharactersServiceImpl.java:**
   - Использует старые enum'ы
   - Не компилируется

3. **CharactersController.java:**
   - Зависит от CharacterMapperMS
   - Не работает

---

## 🔍 КОРНЕВАЯ ПРИЧИНА

### OpenAPI Generator:
- Генерирует DTOs в **ОДНУ директорию**: `src/main/java/.../model/`
- **НЕТ namespace** по API файлам
- **ПОСЛЕДНИЙ** сгенерированный файл **ПОБЕЖДАЕТ**

### Порядок генерации:
```bash
1. character-creation.yaml → GameCharacter (v1) ✅
2. player-management.yaml  → GameCharacter (v2) ❌ ПЕРЕЗАПИСАЛ v1!
```

---

## ✅ РЕШЕНИЯ

### Вариант 1: Разные имена схем ✅ **РЕКОМЕНДУЕТСЯ**

**Изменить OpenAPI specs:**

**character-creation.yaml:**
```yaml
GameCharacter:  # Старое имя
  # ИЛИ
CharacterCreationData:  # Новое имя
```

**player-management.yaml:**
```yaml
PlayerCharacter:  # Новое уникальное имя
  # ИЛИ
ManagedCharacter:  # Другое уникальное имя
```

**Преимущества:**
- ✅ Нет конфликтов
- ✅ Генерация работает автоматически
- ✅ Каждый API = свои DTOs

**Недостатки:**
- ⚠️ Нужно менять OpenAPI specs (в API-SWAGGER)
- ⚠️ Может быть дублирование полей

---

### Вариант 2: Общие схемы через $ref

**Создать shared schemas:**

**api/v1/shared/characters/character-base.yaml:**
```yaml
components:
  schemas:
    CharacterBase:
      type: object
      properties:
        characterId: { type: string }
        name: { type: string }
        # ... общие поля
```

**Использовать в обоих API:**
```yaml
# character-creation.yaml
GameCharacter:
  allOf:
    - $ref: '../shared/characters/character-base.yaml#/components/schemas/CharacterBase'
    - type: object
      properties:
        # Специфичные поля для creation
```

**Преимущества:**
- ✅ DRY - нет дублирования
- ✅ Единая модель для Character
- ✅ Расширение через allOf

**Недостатки:**
- ⚠️ Сложнее поддерживать
- ⚠️ Нужна реорганизация OpenAPI

---

### Вариант 3: Namespace по API файлам

**Настроить OpenAPI Generator:**

```bash
# character-creation.yaml → .../model/charactercreation/
--model-package com.necpgame.backjava.model.charactercreation

# player-management.yaml → .../model/playermanagement/
--model-package com.necpgame.backjava.model.playermanagement
```

**Преимущества:**
- ✅ Изоляция по API файлам
- ✅ Нет конфликтов
- ✅ Можем генерировать всё

**Недостатки:**
- ⚠️ Много субпакетов
- ⚠️ Нужны разные генерации для каждого API
- ⚠️ Сложнее импорты

---

### Вариант 4: Игнорировать конфликты (временно) ⚠️

**Восстановить старый GameCharacter:**
```bash
git checkout HEAD -- src/main/java/.../model/GameCharacter.java
```

**НЕ генерировать player-management** пока не решим проблему

**Преимущества:**
- ✅ Быстрое решение
- ✅ Старый код работает

**Недостатки:**
- ❌ player-management API НЕ реализован
- ❌ Проблема не решена, отложена

---

## 🎯 РЕКОМЕНДАЦИЯ

### Для NECP GAME:

**✅ Вариант 1: Разные имена схем**

**Действия:**
1. Переименовать в API-SWAGGER:
   - `character-creation.yaml`: `GameCharacter` → `CharacterCreationData`
   - `player-management.yaml`: `GameCharacter` → `PlayerCharacter`

2. Перегенерировать контракты

3. Обновить Entities/Mappers

**Оценка:** 2-3 часа работы

---

## 📋 ЗАТРОНУТЫЕ ФАЙЛЫ

### API-SWAGGER (нужны изменения):
- `api/v1/auth/character-creation.yaml` - переименовать GameCharacter
- `api/v1/players/player-management.yaml` - переименовать GameCharacter

### BACK-GO (перегенерировать):
- DTOs в `model/` - новые имена
- CharacterMapperMS - обновить references
- CharactersServiceImpl - обновить references
- CharactersController - обновить references

---

## ⚠️ БЛОКИРУЕТ

### Невозможно реализовать (до решения):
- ❌ player-management.yaml (API-TASK-127)
- ❌ Любые другие APIs с конфликтующими схемами

### Можно реализовать (без конфликтов):
- ✅ inventory-management.yaml (API-TASK-128) - новые схемы
- ✅ loot-system.yaml (API-TASK-129) - новые схемы
- ✅ trade-system.yaml (API-TASK-130) - новые схемы

---

## 🚀 ВРЕМЕННОЕ РЕШЕНИЕ

**Сейчас:**
1. ✅ Восстановлен старый GameCharacter (из character-creation.yaml)
2. ✅ PlayersApi НЕ используется (пока)
3. ✅ Монолит компилируется

**Дальше:**
1. Реализовать APIs без конфликтов (inventory, loot, trade)
2. Решить проблему именования в API-SWAGGER
3. Вернуться к player-management

---

## 📚 ССЫЛКИ

- [OpenAPI Generator - Model Name Mapping](https://openapi-generator.tech/docs/customization/#model-name-mapping)
- [MapStruct Documentation](https://mapstruct.org/documentation/stable/reference/html/)
- API-SWAGGER/api/v1/auth/character-creation.yaml
- API-SWAGGER/api/v1/players/player-management.yaml

---

**Статус:** ⚠️ **БЛОКЕР ВЫЯВЛЕН И ЗАДОКУМЕНТИРОВАН**  
**Решение:** Переименовать схемы в OpenAPI specs  
**Временный workaround:** Использовать character-creation GameCharacter

