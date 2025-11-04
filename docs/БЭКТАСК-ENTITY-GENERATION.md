# Автоматическая генерация JPA Entities и Repositories

## 📋 Обзор

Текущий процесс автоматической генерации из OpenAPI спецификации:

### ✅ Что работает автоматически:

1. **OpenAPI Generator** - генерирует DTOs/Models/Controllers из OpenAPI спецификации
2. **OpenAPI Generator с кастомными шаблонами** - генерирует JPA Entities из OpenAPI схем
3. **Скрипт generate-repositories.ps1** - генерирует Spring Data JPA Repositories на основе entities
4. **Spring Data JPA** - автоматически создает реализации репозиториев из интерфейсов
5. **Maven** - автоматически запускает весь процесс генерации при `mvn clean generate-sources`

### 🚀 Текущий процесс генерации

**Одна команда для генерации всего:**
```bash
mvn clean generate-sources
```

**Что генерируется автоматически:**
1. ✅ **DTOs/Models** - стандартные OpenAPI Generator шаблоны
2. ✅ **Controllers** - стандартные OpenAPI Generator шаблоны (интерфейсы)
3. ✅ **JPA Entities** - кастомный шаблон `Entity.mustache`
4. ✅ **Repositories** - скрипт `scripts/generate-repositories.ps1`

### 🔧 Инструменты для генерации Entities:

#### 1. **IntelliJ IDEA (рекомендуется для разработки)**

**Шаги:**
1. Откройте Database tool window (View → Tool Windows → Database)
2. Подключитесь к PostgreSQL БД
3. Правый клик на схеме БД → Generate Persistence Mapping → By Database Schema
4. Выберите:
   - Package: `com.necpgame.backjava.entity`
   - Persistence provider: JPA
   - Type of fields: Java fields
   - Generate: Entities, Repositories (optional)
5. Нажмите OK

**Преимущества:**
- Работает из коробки
- Поддерживает все типы отношений
- Генерирует аннотации JPA

#### 2. **JPA Buddy Plugin (IntelliJ IDEA)**

**Установка:**
1. File → Settings → Plugins
2. Найдите "JPA Buddy"
3. Установите и перезапустите IDE

**Использование:**
1. Правый клик на схеме БД → JPA Buddy → Generate Entities
2. Настройте опции и генерацию

**Преимущества:**
- Мощный инструмент с множеством опций
- Поддерживает MapStruct маппинг
- Генерация DTOs и Mappers

#### 3. **JHipster (для новых сущностей)**

**Установка:**
```bash
npm install -g generator-jhipster
```

**Использование:**
```bash
cd BACK-GO
jhipster entity <EntityName>
```

**Преимущества:**
- Полная генерация: Entities, Repositories, Services, Controllers
- Генерация тестов
- Поддержка разных типов БД

#### 4. **Hibernate Tools (Ant Task)**

**Настройка:**
Добавьте в `pom.xml`:
```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>hibernate3-maven-plugin</artifactId>
    <version>2.2</version>
    <configuration>
        <components>
            <component>
                <name>hbm2java</name>
                <implementation>jdbcconfiguration</implementation>
            </component>
        </components>
        <componentProperties>
            <jdbcDriver>org.postgresql.Driver</jdbcDriver>
            <jdbcUrl>jdbc:postgresql://localhost:5433/necpgame</jdbcUrl>
            <jdbcUser>necpgame</jdbcUser>
            <jdbcPassword>necpgame</jdbcPassword>
            <packageName>com.necpgame.backjava.entity</packageName>
            <revengFile>src/main/resources/hibernate.reveng.xml</revengFile>
        </componentProperties>
    </configuration>
</plugin>
```

**Использование:**
```bash
mvn hibernate3:hbm2java
```

## 🎯 Текущий подход (рекомендуется)

### Автоматическая генерация из OpenAPI:

**Шаг 1: Генерация всего кода одной командой**
```bash
mvn clean generate-sources
```

**Что происходит автоматически:**
1. OpenAPI Generator генерирует DTOs/Models/Controllers в `target/generated-sources/openapi/`
2. OpenAPI Generator с шаблоном `Entity.mustache` генерирует JPA Entities в `target/generated-sources/entities/`
3. Скрипт `scripts/generate-repositories.ps1` генерирует Repositories в `target/generated-sources/repositories/`
4. Все файлы добавляются в classpath через `build-helper-maven-plugin`

**Шаг 2: Компиляция**
```bash
mvn clean compile
```

**Результат:**
- ✅ DTOs/Models готовы к использованию
- ✅ Controllers (интерфейсы) готовы к реализации
- ✅ JPA Entities готовы к использованию
- ✅ Repositories готовы к использованию (Spring Data JPA создаст реализации)

**Шаг 3: Реализация Controllers и Services (вручную)**
```java
// Реализация Controller
@RestController
@RequiredArgsConstructor
public class AccountController implements AccountApi {
    private final AccountService service;
    // ...
}

// Создание Service
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository repository;
    
    public AccountEntity create(AccountEntity account) {
        return repository.save(account);
    }
}
```

## ⚠️ Важные замечания

1. **Генерация автоматическая** - все генерируется из OpenAPI спецификации одной командой
2. **Размер файлов** - каждый Entity не должен превышать 400 строк (если больше - оптимизировать OpenAPI)
3. **Используйте Lombok** для уменьшения boilerplate кода
4. **Repositories** - Spring Data JPA автоматически создает реализации из интерфейсов
5. **Сгенерированные файлы** - находятся в `target/generated-sources/`, не редактировать вручную
6. **Шаблоны универсальные** - один набор шаблонов работает для всех OpenAPI файлов

## 📁 Структура сгенерированных файлов

```
target/generated-sources/
├── openapi/                    # DTOs/Models/Controllers (стандартные шаблоны)
│   └── src/main/java/com/necpgame/backjava/
│       ├── api/                # Controller интерфейсы
│       └── model/               # DTOs/Models
├── entities/                    # JPA Entities (кастомный шаблон Entity.mustache)
│   └── src/main/java/com/necpgame/backjava/entity/
└── repositories/                # Repositories (скрипт generate-repositories.ps1)
    └── src/main/java/com/necpgame/backjava/repository/
```

## 🔗 Полезные ссылки

- [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Hibernate Documentation](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html)
- [JPA Buddy Documentation](https://www.jpa-buddy.com/documentation/)

