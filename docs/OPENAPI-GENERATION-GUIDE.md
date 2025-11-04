# OpenAPI Generation Guide

## 📖 Обзор

Этот документ описывает процесс генерации кода из OpenAPI спецификаций для проекта NECPGAME Backend.

**Главный принцип:** OpenAPI спецификация является **единственным источником правды** для генерации всех MVC слоёв.

## 🎯 Что генерируется

Из одной OpenAPI спецификации генерируются все необходимые слои:

1. **DTOs и API Interfaces** - модели данных и REST API интерфейсы
2. **JPA Entities** - сущности для работы с БД через Hibernate
3. **Spring Data Repositories** - репозитории для CRUD операций
4. **Service Interfaces** - интерфейсы бизнес-логики
5. **REST Controllers** - Spring MVC контроллеры

## 🚀 Быстрый старт

### Генерация всех слоёв

```powershell
# Из корня проекта BACK-GO
.\scripts\generate-openapi-layers.ps1
```

Эта команда:
- Очистит `target/generated-sources/`
- Сгенерирует все 5 слоёв из `character-creation.yaml`
- Покажет статистику генерации

### Генерация из другого API файла

```powershell
.\scripts\generate-openapi-layers.ps1 -ApiSpec ../API-SWAGGER/api/v1/character/cyberpsychosis.yaml
```

### Генерация только определённых слоёв

```powershell
# Только Controllers и Services
.\scripts\generate-openapi-layers.ps1 -Layers Controllers,Services

# Только Entities
.\scripts\generate-openapi-layers.ps1 -Layers Entities
```

Доступные слои: `DTOs`, `Entities`, `Repositories`, `Services`, `Controllers`, `All`

## 📁 Структура генерации

```
target/generated-sources/
├── openapi/          # DTOs + API Interfaces
│   └── src/main/java/com/necpgame/backjava/
│       ├── api/      # AuthApi, CharactersApi, etc.
│       └── model/    # Account, Character, LoginRequest, etc.
│
├── entities/         # JPA Entities
│   └── src/main/java/com/necpgame/backjava/
│       └── entity/   # AccountEntity, CharacterEntity, etc.
│
├── repositories/     # Spring Data Repositories
│   └── src/main/java/com/necpgame/backjava/
│       └── repository/  # AccountRepository, CharacterRepository, etc.
│
├── services/         # Service Interfaces
│   └── src/main/java/com/necpgame/backjava/
│       └── service/  # AuthService, CharactersService, etc.
│
└── controllers/      # REST Controllers
    └── src/main/java/com/necpgame/backjava/
        └── controller/  # AuthApiController, CharactersApiController, etc.
```

## 🔧 Кастомные шаблоны

Проект использует кастомные Mustache шаблоны для генерации кода с нужной структурой:

### Активные шаблоны (в `templates/`)

1. **`api.mustache`** - генерирует Service интерфейсы
   ```java
   public interface AuthService {
       LoginResponse login(LoginRequest request);
   }
   ```

2. **`apiController.mustache`** - генерирует REST контроллеры
   ```java
   @Controller
   public class AuthApiController implements AuthApi {
       // Полная реализация с Spring MVC аннотациями
   }
   ```

3. **`model.mustache`** - генерирует JPA Entities
   ```java
   @Entity
   @Table(name = "account")
   public class Account {
       @Id
       @GeneratedValue(strategy = GenerationType.UUID)
       private UUID id;
       // + Lombok, timestamps, validation
   }
   ```

4. **`repositoryModel.mustache`** - генерирует Spring Data репозитории
   ```java
   @Repository
   public interface AccountRepository extends JpaRepository<Account, UUID> {
       // Spring Data автоматически реализует CRUD
   }
   ```

## 🔄 Интеграция с Maven

Хотя основная генерация происходит через PowerShell скрипт, в `pom.xml` настроен `build-helper-maven-plugin`, который автоматически добавляет сгенерированные источники в classpath:

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>build-helper-maven-plugin</artifactId>
    <execution>
        <phase>generate-sources</phase>
        <goals>
            <goal>add-source</goal>
        </goals>
    </execution>
</plugin>
```

Это позволяет Maven видеть сгенерированный код при компиляции.

## 📝 ServiceImpl - ручное создание

**ServiceImpl классы НЕ генерируются автоматически** и создаются вручную по следующим причинам:

1. OpenAPI Generator Spring не поддерживает отдельную генерацию ServiceImpl
2. ServiceImpl содержит бизнес-логику, которую невозможно сгенерировать из спецификации
3. Ручное создание даёт больше контроля над реализацией

### Пример создания ServiceImpl

```java
package com.necpgame.backjava.service.impl;

import com.necpgame.backjava.service.AuthService;
import com.necpgame.backjava.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for: {}", request.getEmail());
        
        // Реализация бизнес-логики
        Account account = accountRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        
        // ... проверка пароля, создание токена и т.д.
        
        return new LoginResponse(token);
    }
}
```

## 🛠️ Параметры генерации

### Общие параметры для всех слоёв

```bash
-g spring                    # Spring generator
-t templates                 # Использовать кастомные шаблоны
useSpringBoot3=true         # Spring Boot 3.x
useJakartaEe=true           # Jakarta EE (javax -> jakarta)
```

### Специфичные параметры

#### DTOs & API Interfaces
```bash
interfaceOnly=true          # Только интерфейсы, без реализации
useBeanValidation=true      # Jakarta Validation аннотации
openApiNullable=false       # Не использовать JsonNullable
```

#### JPA Entities
```bash
generateApis=false                              # Не генерировать API
generateModels=true                             # Генерировать модели
modelTemplateFiles=model.mustache=Entity.java  # Кастомный шаблон
```

#### Service Interfaces
```bash
interfaceOnly=true          # Только интерфейсы
--api-name-suffix Service   # Добавить суффикс Service к именам
```

#### Controllers
```bash
interfaceOnly=false         # Генерировать классы, не интерфейсы
delegatePattern=false       # Не использовать Delegate pattern
```

## 🔍 Проверка результатов

После генерации проверьте:

1. **Количество файлов** - должны быть сгенерированы все сущности из OpenAPI
2. **Компиляция** - `mvn compile` должна пройти без ошибок
3. **Импорты** - проверьте, что используется `jakarta.*`, а не `javax.*`
4. **Аннотации** - JPA, Spring, Validation должны быть на месте

## 📚 Дополнительные ресурсы

- [OpenAPI Generator Documentation](https://openapi-generator.tech/docs/generators/spring)
- [Mustache Template Syntax](https://mustache.github.io/mustache.5.html)
- [Spring Boot 3 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide)

## ❓ FAQ

### Почему не используется Maven plugin для генерации?

PowerShell скрипт проще, понятнее и даёт больше контроля:
- Легко отлаживать каждую команду
- Можно генерировать отдельные слои
- Не зависит от Maven lifecycle quirks
- Прозрачность выполнения

### Как добавить новый OpenAPI файл для генерации?

Просто укажите путь к файлу:
```powershell
.\scripts\generate-openapi-layers.ps1 -ApiSpec ../API-SWAGGER/api/v1/new/feature.yaml
```

### Что делать, если генерация падает с ошибкой?

1. Проверьте корректность OpenAPI спецификации
2. Убедитесь, что Node.js и npx установлены
3. Проверьте наличие `templates/` директории с шаблонами
4. Запустите генерацию отдельного слоя для отладки:
   ```powershell
   .\scripts\generate-openapi-layers.ps1 -Layers DTOs
   ```

### Можно ли изменить структуру генерируемого кода?

Да! Отредактируйте соответствующий Mustache шаблон в `templates/`:
- `api.mustache` - Service интерфейсы
- `apiController.mustache` - Controllers
- `model.mustache` - JPA Entities  
- `repositoryModel.mustache` - Repositories

После изменения шаблона перегенерируйте код.

## 🔄 Workflow разработки

1. **Изменяем OpenAPI спецификацию** в репозитории `API-SWAGGER`
2. **Генерируем код**: `.\scripts\generate-openapi-layers.ps1`
3. **Компилируем**: `mvn compile`
4. **Создаём ServiceImpl** (если нужны новые сервисы)
5. **Реализуем бизнес-логику** в ServiceImpl
6. **Тестируем**: пишем unit и integration тесты
7. **Коммитим** изменения через `.\scripts\autocommit.ps1`
