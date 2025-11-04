# Решение: Генерация миграций через Liquibase из JPA Entities

## 📋 Обзор

Использование **только Liquibase** для генерации миграций из JPA Entities. Полная замена Flyway на Liquibase.

## ✅ Преимущества Liquibase

1. **Автоматическая генерация changelog из JPA Entities** через `diffChangeLog`
2. **Поддержка версионности** миграций
3. **Множественные форматы** changelog (XML, YAML, JSON, SQL)
4. **Интеграция с Spring Boot** из коробки
5. **Сравнение схем** - автоматическое определение изменений

## 🚀 Реализация

### Шаг 1: Добавить зависимость Liquibase

В `pom.xml` заменить Flyway на Liquibase:

```xml
<!-- Удалить Flyway -->
<!-- <dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency> -->

<!-- Добавить Liquibase -->
<dependency>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-core</artifactId>
</dependency>
```

### Шаг 2: Добавить Liquibase Maven Plugin

В `pom.xml` добавить плагин для генерации changelog:

```xml
<plugin>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-maven-plugin</artifactId>
    <version>4.23.0</version>
    <configuration>
        <propertyFile>src/main/resources/liquibase.properties</propertyFile>
        <changeLogFile>src/main/resources/db/changelog/db.changelog-master.xml</changeLogFile>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>org.liquibase</groupId>
            <artifactId>liquibase-core</artifactId>
            <version>4.23.0</version>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>42.7.1</version>
        </dependency>
    </dependencies>
</plugin>
```

### Шаг 3: Настроить application.yml

Заменить Flyway на Liquibase:

```yaml
spring:
  liquibase:
    enabled: true
    change-log: classpath:db/changelog/db.changelog-master.xml
    default-schema: public
    drop-first: false
```

### Шаг 4: Создать файл liquibase.properties

Создать `src/main/resources/liquibase.properties`:

```properties
changeLogFile=src/main/resources/db/changelog/db.changelog-master.xml
url=jdbc:postgresql://localhost:5433/necpgame
username=necpgame
password=necpgame
driver=org.postgresql.Driver
```

### Шаг 5: Генерация changelog из JPA Entities

**Вариант A: Через Hibernate SchemaExport + Liquibase diffChangeLog**

1. Создать базу данных из JPA Entities через Hibernate:
```bash
# Временно включить hbm2ddl.auto=create для создания схемы
# Или использовать Hibernate SchemaExport
```

2. Сгенерировать changelog из существующей базы данных:
```bash
mvn liquibase:generateChangeLog
```

3. Сравнить текущую схему с JPA Entities:
```bash
mvn liquibase:diffChangeLog
```

**Вариант B: Через Liquibase Maven Plugin с Hibernate поддержкой (РЕКОМЕНДУЕТСЯ)**

Использовать стандартный Liquibase Maven Plugin с поддержкой Hibernate для автоматической генерации changelog из JPA Entities.

1. Настроить Liquibase Maven Plugin в `pom.xml` с `referenceUrl=hibernate:spring`:
```xml
<plugin>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-maven-plugin</artifactId>
    <configuration>
        <referenceUrl>hibernate:spring:com.necpgame.backjava.entity?dialect=org.hibernate.dialect.PostgreSQLDialect</referenceUrl>
        <referenceDriver>liquibase.ext.hibernate.database.connection.HibernateDriver</referenceDriver>
        <changeLogFile>src/main/resources/db/changelog/db.changelog-master.xml</changeLogFile>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>org.liquibase.ext</groupId>
            <artifactId>liquibase-hibernate5</artifactId>
            <version>3.6</version>
        </dependency>
    </dependencies>
</plugin>
```

2. Сгенерировать changelog из JPA Entities:
```bash
mvn liquibase:diffChangeLog
```

**Преимущества:**
- ✅ Использует стандартные инструменты Liquibase
- ✅ Автоматически находит все Entity классы через Hibernate
- ✅ Не требует создания временной БД
- ✅ Работает напрямую с JPA Entities

## 📝 Процесс генерации миграций

### Автоматическая генерация через Maven

1. **Сгенерировать JPA Entities из OpenAPI:**
```bash
mvn clean generate-sources
```

2. **Создать базу данных из JPA Entities (временно):**
```bash
# Временно изменить hibernate.ddl-auto=create
# Или использовать Hibernate SchemaExport
```

3. **Сгенерировать changelog из базы данных:**
```bash
mvn liquibase:generateChangeLog
```

4. **Для последующих изменений - использовать diff:**
```bash
mvn liquibase:diffChangeLog
```

## 🔧 Интеграция в процесс сборки

Добавить в `pom.xml` execution для автоматической генерации:

```xml
<plugin>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>generate-changelog</id>
            <phase>generate-sources</phase>
            <goals>
                <goal>generateChangeLog</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## ⚠️ Важные замечания

1. **Liquibase Maven Plugin с Hibernate поддержкой** автоматически находит все Entity классы через сканирование пакета
2. **Для работы diffChangeLog** с Hibernate нужно указать `referenceUrl=hibernate:spring:package.name`
3. **Требуется зависимость** `liquibase-hibernate5` для поддержки Hibernate
4. **Entity классы должны быть скомпилированы** перед генерацией changelog

## 🎯 Рекомендуемый подход

**Использование стандартного Liquibase Maven Plugin с Hibernate поддержкой:**

1. Настроить Liquibase Maven Plugin с `referenceUrl=hibernate:spring:com.necpgame.backjava.entity`
2. Добавить зависимость `liquibase-hibernate5` в плагин
3. Использовать команду `mvn liquibase:diffChangeLog` для генерации changelog из JPA Entities
4. Использовать Liquibase для управления миграциями в продакшене

**Преимущества:**
- ✅ Используются стандартные инструменты Liquibase
- ✅ Автоматическая генерация из JPA Entities без создания временной БД
- ✅ Поддержка версионности
- ✅ Интеграция с Spring Boot
- ✅ Не требует дополнительных Java программ

---

## 🔗 Полезные ссылки

- [Liquibase Documentation](https://docs.liquibase.com/)
- [Liquibase Maven Plugin](https://docs.liquibase.com/tools-integrations/maven/home.html)
- [Spring Boot Liquibase Integration](https://docs.spring.io/spring-boot/docs/current/reference/html/data.html#data.sql.liquibase)

