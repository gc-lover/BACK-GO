# Руководство по генерации кода из OpenAPI

## ⚙️ Генерация через CLI OpenAPI Generator

**Важно:** Генерация выполняется через CLI OpenAPI Generator, а не через Maven Plugin!

### ✅ Преимущества CLI подхода:
- Нет хардкода в `pom.xml`
- Универсальное решение для любых OpenAPI файлов
- Гибкая конфигурация через параметры командной строки
- Поддержка пакетной генерации из нескольких файлов

### ✅ Шаблоны универсальные
**Важно:** Созданные Mustache шаблоны в `templates/` являются **универсальными** и работают для **всех** OpenAPI файлов!

- ✅ `model.mustache` - генерирует JPA Entities для всех схем
- ✅ `Repository.mustache` - генерирует Spring Data Repositories для всех схем
- ✅ `Service.mustache` - генерирует Service интерфейсы для всех операций
- ✅ `ServiceImpl.mustache` - генерирует Service реализации для всех операций
- ✅ `apiController.mustache` - генерирует Controller реализации для всех операций

**Не нужно создавать новые шаблоны для каждого OpenAPI файла!**

## 🚀 Быстрый старт

### 1. Генерация через Maven (рекомендуется)

```bash
# Генерация из конкретного OpenAPI файла
mvn clean generate-sources -Dopenapi.spec=../API-SWAGGER/api/v1/auth/character-creation.yaml -Dskip.openapi.generation=false

# Или из другого файла
mvn clean generate-sources -Dopenapi.spec=../API-SWAGGER/api/v1/gameplay/combat/cyberpsychosis.yaml -Dskip.openapi.generation=false
```

**Важно:** Необходимо указать оба параметра:
- `-Dopenapi.spec=путь/к/файлу.yaml` - путь к OpenAPI файлу
- `-Dskip.openapi.generation=false` - включить генерацию (по умолчанию отключена)

### 2. Генерация через CLI (альтернатива)

**Установка CLI:**
```bash
npm install -g @openapitools/openapi-generator-cli
```

**Пример генерации DTOs:**
```bash
npx --yes @openapitools/openapi-generator-cli generate \
  -i ../API-SWAGGER/api/v1/auth/character-creation.yaml \
  -g spring \
  -o target/generated-sources/openapi \
  --api-package com.necpgame.backjava.api \
  --model-package com.necpgame.backjava.model \
  -p "interfaceOnly=true,useSpringBoot3=true,useJakartaEe=true"
```

**⚠️ Важно:** Список свойств в `-p` должен быть в кавычках!

## Как использовать для разных OpenAPI файлов

### Вариант 1: Через CLI параметры (рекомендуется)

Используй параметр `-i` для указания пути к OpenAPI файлу:

```bash
# Генерация из конкретного файла
openapi-generator-cli generate -i ../API-SWAGGER/api/v1/auth/character-creation.yaml ...

# Или из другого файла
openapi-generator-cli generate -i ../API-SWAGGER/api/v1/gameplay/combat/cyberpsychosis.yaml ...
```

### Вариант 2: Через bash-скрипт для пакетной генерации

Создай bash-скрипт `generate.sh` (см. [GENERATION-COMMANDS.md](./GENERATION-COMMANDS.md)):

```bash
#!/bin/bash
OPENAPI_FILE=$1

# Генерация всех слоёв одним скриптом
./generate.sh ../API-SWAGGER/api/v1/auth/character-creation.yaml
./generate.sh ../API-SWAGGER/api/v1/gameplay/combat/cyberpsychosis.yaml
```

### Вариант 3: Генерация из всех файлов в директории

```bash
# Генерация из всех OpenAPI файлов в директории
for file in ../API-SWAGGER/api/v1/**/*.yaml; do
  echo "Генерация из $file"
  openapi-generator-cli generate -i "$file" ...
done
```

## Структура шаблонов

### Где находятся шаблоны?
```
BACK-GO/templates/
├── Entity.mustache        # Универсальный шаблон для JPA Entities
├── Repository.mustache    # Универсальный шаблон для Repositories
├── Service.mustache       # Универсальный шаблон для Service интерфейсов
├── ServiceImpl.mustache   # Универсальный шаблон для Service реализаций
└── Migration.mustache     # Универсальный шаблон для Flyway миграций
```

### Как работают шаблоны?

OpenAPI Generator автоматически:
1. Парсит OpenAPI спецификацию
2. Извлекает схемы из `components/schemas`
3. Извлекает операции из `paths`
4. Применяет шаблоны к каждой схеме/операции
5. Подставляет переменные из OpenAPI спецификации

**Переменные в шаблонах:**
- `{{classname}}` - имя класса из OpenAPI схемы
- `{{vars}}` - список полей из OpenAPI схемы
- `{{datatype}}` - тип данных из OpenAPI
- `{{required}}` - обязательность поля
- И многие другие (см. документацию OpenAPI Generator)

## Процесс генерации

### 1. Базовый код (DTOs, Models, API Interfaces)
- Использует стандартные шаблоны OpenAPI Generator
- Генерируется в `target/generated-sources/openapi`
- Команда в [GENERATION-COMMANDS.md](./GENERATION-COMMANDS.md#1-генерация-dtos-и-api-interfaces)

### 2. JPA Entities
- Использует кастомный шаблон `model.mustache`
- Генерируется в `target/generated-sources/entities`
- Применяется для каждой схемы в `components/schemas`
- Команда в [GENERATION-COMMANDS.md](./GENERATION-COMMANDS.md#2-генерация-jpa-entities)

### 3. Repositories
- Использует кастомный шаблон `Repository.mustache`
- Генерируется в `target/generated-sources/repositories`
- Применяется для каждой схемы в `components/schemas`
- Генерирует Spring Data JPA Repository интерфейсы
- Команда в [GENERATION-COMMANDS.md](./GENERATION-COMMANDS.md#3-генерация-repositories)

### 4. Services
- Использует кастомные шаблоны `Service.mustache` и `ServiceImpl.mustache`
- Генерируется в `target/generated-sources/services`
- Применяется для каждой API группы (tag) в `paths`
- Генерирует Service интерфейсы и ServiceImpl классы с заглушками
- Команды в [GENERATION-COMMANDS.md](./GENERATION-COMMANDS.md#4-генерация-service-интерфейсов)

### 5. Controllers
- Использует кастомный шаблон `apiController.mustache`
- Генерируется в `target/generated-sources/controllers`
- Применяется для каждой API группы (tag) в `paths`
- Генерирует Controller реализации с базовой логикой
- Команда в [GENERATION-COMMANDS.md](./GENERATION-COMMANDS.md#6-генерация-controllers)

### 6. Liquibase Migrations
- Генерируется из JPA Entities через Liquibase Maven Plugin
- Генерируется в `src/main/resources/db/changelog/`
- Команда: `mvn liquibase:diffChangeLog`
- Применяется после компиляции Entities: `mvn clean compile && mvn liquibase:diffChangeLog`

## Примеры использования

### Пример 1: Генерация из одного файла
```bash
# Генерация из character-creation.yaml
mvn clean generate-sources -Dopenapi.spec=../API-SWAGGER/api/v1/auth/character-creation.yaml
```

### Пример 2: Генерация из нескольких файлов
Добавь несколько execution в `pom.xml`:
```xml
<execution>
    <id>generate-character-creation</id>
    <configuration>
        <inputSpec>${project.basedir}/../API-SWAGGER/api/v1/auth/character-creation.yaml</inputSpec>
        <!-- ... -->
    </configuration>
</execution>
<execution>
    <id>generate-cyberpsychosis</id>
    <configuration>
        <inputSpec>${project.basedir}/../API-SWAGGER/api/v1/gameplay/combat/cyberpsychosis.yaml</inputSpec>
        <!-- ... -->
    </configuration>
</execution>
```

### Пример 3: Использование профилей
```bash
# Генерация с профилем
mvn clean generate-sources -Pcharacter-creation
```

## Важные замечания

### ⚠️ Один набор шаблонов для всех файлов
- ✅ Шаблоны универсальные - работают для всех OpenAPI файлов
- ✅ Не нужно создавать новые шаблоны для каждого файла
- ✅ Можно использовать один набор шаблонов для всех спецификаций

### ⚠️ Конфигурация в pom.xml
- `inputSpec` - путь к OpenAPI файлу (можно менять через свойства)
- `templateDirectory` - путь к шаблонам (один для всех)
- `output` - директория для сгенерированных файлов

### ⚠️ Именование файлов
OpenAPI Generator автоматически создает имена файлов на основе:
- Имен схем из `components/schemas`
- Имен операций из `paths`
- Имен классов из OpenAPI спецификации

**Не нужно указывать имена файлов в шаблонах!**

## Документация

- [OpenAPI Generator Templates](https://openapi-generator.tech/docs/templating)
- [Mustache Manual](https://mustache.github.io/mustache.5.html)
- [OpenAPI Generator Variables](https://openapi-generator.tech/docs/generators)


