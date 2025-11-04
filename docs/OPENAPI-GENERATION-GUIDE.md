# Руководство по генерации кода из OpenAPI

## Общие принципы

### ✅ Шаблоны универсальные
**Важно:** Созданные Mustache шаблоны в `templates/` являются **универсальными** и работают для **всех** OpenAPI файлов!

- ✅ `Entity.mustache` - работает для всех схем
- ✅ `Repository.mustache` - работает для всех схем
- ✅ `Service.mustache` - работает для всех операций
- ✅ `ServiceImpl.mustache` - работает для всех операций
- ✅ `Migration.mustache` - работает для всех схем

**Не нужно создавать новые шаблоны для каждого OpenAPI файла!**

## Как использовать для разных OpenAPI файлов

### Вариант 1: Через Maven свойства (рекомендуется)

Используй свойство `openapi.spec` для указания пути к OpenAPI файлу:

```bash
# Генерация из конкретного файла
mvn clean generate-sources -Dopenapi.spec=../API-SWAGGER/api/v1/auth/character-creation.yaml

# Или из другого файла
mvn clean generate-sources -Dopenapi.spec=../API-SWAGGER/api/v1/gameplay/combat/cyberpsychosis.yaml
```

### Вариант 2: Через Maven профили

Можно создать профили для разных OpenAPI файлов в `pom.xml`:

```xml
<profiles>
    <profile>
        <id>character-creation</id>
        <properties>
            <openapi.spec>${project.basedir}/../API-SWAGGER/api/v1/auth/character-creation.yaml</openapi.spec>
        </properties>
    </profile>
    <profile>
        <id>cyberpsychosis</id>
        <properties>
            <openapi.spec>${project.basedir}/../API-SWAGGER/api/v1/gameplay/combat/cyberpsychosis.yaml</openapi.spec>
        </properties>
    </profile>
</profiles>
```

Использование:
```bash
mvn clean generate-sources -Pcharacter-creation
mvn clean generate-sources -Pcyberpsychosis
```

### Вариант 3: Добавить новый execution в pom.xml

Если нужно генерировать из нескольких OpenAPI файлов одновременно, можно добавить дополнительные execution:

```xml
<execution>
    <id>generate-api-cyberpsychosis</id>
    <phase>generate-sources</phase>
    <goals>
        <goal>generate</goal>
    </goals>
    <configuration>
        <inputSpec>${project.basedir}/../API-SWAGGER/api/v1/gameplay/combat/cyberpsychosis.yaml</inputSpec>
        <!-- остальная конфигурация -->
    </configuration>
</execution>
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

### 1. Базовый код (DTOs, Models, Controllers)
- Использует стандартные шаблоны OpenAPI Generator
- Генерируется в `target/generated-sources/openapi`

### 2. JPA Entities
- Использует кастомный шаблон `Entity.mustache`
- Генерируется в `target/generated-sources/entities`
- Применяется для каждой схемы в `components/schemas`

### 3. Repositories
- Использует скрипт `scripts/generate-repositories.ps1`
- Генерируется в `target/generated-sources/repositories`
- Скрипт запускается автоматически через `exec-maven-plugin` после генерации entities
- Генерирует репозитории для основных Entity классов (Account, Character, CharacterClass, CharacterOrigin, City, Faction)

### 4. Services
- Использует кастомные шаблоны `Service.mustache` и `ServiceImpl.mustache`
- Генерируется в `target/generated-sources/services`
- Применяется для каждой операции в `paths`

### 5. Migrations
- Использует кастомный шаблон `Migration.mustache`
- Генерируется в `target/generated-sources/migrations`
- Применяется для каждой схемы в `components/schemas`

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


