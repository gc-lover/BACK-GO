# Структура шаблонов OpenAPI Generator

## 📁 Активные шаблоны в `templates/`

### Кастомные Mustache шаблоны для генерации MVC слоёв

**Все шаблоны используются через OpenAPI Generator CLI с параметром `-t templates`**

### Список активных шаблонов:

1. **`api.mustache`** → Service интерфейсы ✅
   - Генерирует: `*Service.java` в пакете `com.necpgame.backjava.service`
   - Параметр: `--api-name-suffix Service` + `interfaceOnly=true`
   - Содержит: чистые Java интерфейсы без Spring MVC аннотаций
   - Пример: `AuthService.java`, `CharactersService.java`

2. **`apiController.mustache`** → REST Controllers ✅
   - Генерирует: `*ApiController.java` в пакете `com.necpgame.backjava.controller`
   - Параметр: `interfaceOnly=false` + `delegatePattern=false`
   - Содержит: Spring `@Controller` классы, реализующие API интерфейсы
   - Пример: `AuthApiController.java implements AuthApi`

3. **`model.mustache`** → JPA Entities ✅
   - Генерирует: `*Entity.java` в пакете `com.necpgame.backjava.entity`
   - Параметр: `modelTemplateFiles=model.mustache=Entity.java`
   - Содержит: JPA аннотации, Lombok, timestamps, UUID id
   - Пример: `AccountEntity.java`, `CharacterEntity.java`

4. **`repositoryModel.mustache`** → Spring Data Repositories ✅
   - Генерирует: `*Repository.java` в пакете `com.necpgame.backjava.repository`
   - Параметр: `modelTemplateFiles=repositoryModel.mustache=Repository.java`
   - Содержит: Spring Data JPA интерфейс с базовыми CRUD операциями
   - Пример: `AccountRepository.java extends JpaRepository`

### Неактивные шаблоны:

5. **`serviceImpl.mustache`** → Service реализации (НЕ ИСПОЛЬЗУЕТСЯ)
   - **Статус**: Отключено в Maven
   - **Причина**: OpenAPI Generator Spring не поддерживает отдельную генерацию ServiceImpl
   - **Решение**: ServiceImpl создаются вручную по мере необходимости

## 🎯 Принцип работы

Все шаблоны работают через **переопределение стандартных Mustache файлов OpenAPI Generator**:

- **API шаблоны** (`api.mustache`, `serviceImpl.mustache`, `apiController.mustache`) используют контекст операций (endpoints)
- **Model шаблоны** (`model.mustache`, `repositoryModel.mustache`) используют контекст схем (schemas)

### Параметры генерации:

```bash
# Service интерфейсы
-p "interfaceOnly=true,generateApis=true,generateModels=false,apiTemplateFiles=api.mustache=Service.java"

# ServiceImpl заглушки
-p "interfaceOnly=false,generateApis=true,generateModels=false,apiTemplateFiles=serviceImpl.mustache=ServiceImpl.java"

# Controllers
-p "interfaceOnly=false,generateApis=true,generateModels=false,apiTemplateFiles=apiController.mustache=Controller.java"

# JPA Entities
-p "generateApis=false,generateModels=true,modelTemplateFiles=model.mustache=Entity.java"

# Repositories
-p "generateApis=false,generateModels=true,modelTemplateFiles=repositoryModel.mustache=Repository.java"
```

## 🔄 Процесс генерации (через PowerShell скрипт)

Генерация происходит через универсальный PowerShell скрипт `scripts/generate-openapi-layers.ps1`:

### Режимы работы:

#### 1. Генерация из одного файла
```powershell
.\scripts\generate-openapi-layers.ps1 -ApiSpec ../API-SWAGGER/api/v1/auth/character-creation.yaml
```

#### 2. Генерация из всей директории (обрабатывает ВСЕ .yaml файлы)
```powershell
.\scripts\generate-openapi-layers.ps1 -ApiDirectory ../API-SWAGGER/api/v1/
```

#### 3. Генерация только определённых слоёв
```powershell
.\scripts\generate-openapi-layers.ps1 -ApiSpec path/to/api.yaml -Layers Controllers,Services
```

### Генерируемые слои:

1. **DTOs + API Interfaces** → `target/generated-sources/openapi/`
   - Стандартная генерация без кастомных шаблонов
   - Пакеты: `com.necpgame.backjava.api`, `com.necpgame.backjava.model`

2. **JPA Entities** → `target/generated-sources/entities/`
   - Шаблон: `model.mustache`
   - Пакет: `com.necpgame.backjava.entity`

3. **Repositories** → `target/generated-sources/repositories/`
   - Шаблон: `repositoryModel.mustache`
   - Пакет: `com.necpgame.backjava.repository`

4. **Service интерфейсы** → `target/generated-sources/services/`
   - Шаблон: `api.mustache`
   - Пакет: `com.necpgame.backjava.service`

5. **Controllers** → `target/generated-sources/controllers/`
   - Шаблон: `apiController.mustache`
   - Пакет: `com.necpgame.backjava.controller`

### Преимущества скрипта:
- ✅ **Без хардкода** - требует явного указания файла или директории
- ✅ **Прозрачность** - видим каждую команду генерации
- ✅ **Гибкость** - можно генерировать отдельные слои
- ✅ **Статистика** - показывает успешные/неудачные файлы
- ✅ **Обработка ошибок** - продолжает работу после ошибок

## 📋 Как добавить новый шаблон

1. Создай `.mustache` файл в `templates/`
2. Добавь секцию генерации в скрипт `scripts/generate-openapi-layers.ps1`
3. Укажи параметр `-p` с `apiTemplateFiles` или `modelTemplateFiles`

## 🚀 Быстрый старт

```powershell
# Генерация из одного файла (все слои)
.\scripts\generate-openapi-layers.ps1 -ApiSpec ../API-SWAGGER/api/v1/auth/character-creation.yaml

# Генерация из всей директории (обработает все .yaml файлы)
.\scripts\generate-openapi-layers.ps1 -ApiDirectory ../API-SWAGGER/api/v1/

# Генерация только контроллеров
.\scripts\generate-openapi-layers.ps1 -ApiSpec path/to/api.yaml -Layers Controllers
```

## 📚 Дополнительная информация

- [OpenAPI Generator Templates](https://openapi-generator.tech/docs/templating)
- [Mustache Manual](https://mustache.github.io/mustache.5.html)
- См. `templates/README.md` для подробностей о кастомных шаблонах

