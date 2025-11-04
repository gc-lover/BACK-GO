# Структура шаблонов OpenAPI Generator

## 📁 Активные шаблоны в `templates/`

### Кастомные Mustache шаблоны для генерации MVC слоёв

**Все шаблоны используются через OpenAPI Generator CLI с параметром `-t templates`**

### Список шаблонов:

1. **`api.mustache`** → Service интерфейсы
   - Генерирует: `*ApiService.java` в пакете `com.necpgame.backjava.service`
   - Используется: `-p "apiTemplateFiles=api.mustache=Service.java"`
   - Содержит: чистые Java интерфейсы без Spring MVC аннотаций

2. **`serviceImpl.mustache`** → Service реализации (заглушки)
   - Генерирует: `*ApiServiceImpl.java` в пакете `com.necpgame.backjava.service.impl`
   - Используется: `-p "apiTemplateFiles=serviceImpl.mustache=ServiceImpl.java"`
   - Содержит: stub методы с TODO и логированием

3. **`apiController.mustache`** → REST Controllers
   - Генерирует: `*ApiController.java` в пакете `com.necpgame.backjava.controller`
   - Используется: `-p "apiTemplateFiles=apiController.mustache=Controller.java"`
   - Содержит: полную реализацию контроллера с делегированием в Service

4. **`model.mustache`** → JPA Entities
   - Генерирует: `*Entity.java` в пакете `com.necpgame.backjava.entity`
   - Используется: `-p "modelTemplateFiles=model.mustache=Entity.java"`
   - Содержит: JPA аннотации, Lombok, timestamps, UUID id

5. **`repositoryModel.mustache`** → Spring Data Repositories
   - Генерирует: `*Repository.java` в пакете `com.necpgame.backjava.repository`
   - Используется: `-p "modelTemplateFiles=repositoryModel.mustache=Repository.java"`
   - Содержит: Spring Data JPA интерфейс с базовыми CRUD операциями

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

## 🔄 Процесс генерации (через Maven)

Генерация происходит через `exec-maven-plugin` с вызовом `npx @openapitools/openapi-generator-cli`:

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

5. **ServiceImpl заглушки** → `target/generated-sources/services/`
   - Шаблон: `serviceImpl.mustache`
   - Пакет: `com.necpgame.backjava.service.impl`

6. **Controllers** → `target/generated-sources/controllers/`
   - Шаблон: `apiController.mustache`
   - Пакет: `com.necpgame.backjava.controller`

## 📋 Как добавить новый шаблон

1. Создай `.mustache` файл в `templates/`
2. Добавь `<execution>` в `pom.xml` с нужными параметрами
3. Укажи параметр `-p` с `apiTemplateFiles` или `modelTemplateFiles`

## 📚 Дополнительная информация

- [OpenAPI Generator Templates](https://openapi-generator.tech/docs/templating)
- [Mustache Manual](https://mustache.github.io/mustache.5.html)
- См. `templates/README.md` для подробностей о кастомных шаблонах

