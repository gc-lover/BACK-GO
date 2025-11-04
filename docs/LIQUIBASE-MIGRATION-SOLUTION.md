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

**Вариант B: Через Java программу с Liquibase API**

Создать Java программу, которая:
1. Использует Hibernate для создания схемы из JPA Entities
2. Использует Liquibase API для генерации changelog

```java
package com.necpgame.backjava.generator;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.diff.DiffGeneratorFactory;
import liquibase.diff.DiffResult;
import liquibase.diff.output.DiffOutputControl;
import liquibase.diff.output.changelog.DiffToChangeLog;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.FileSystemResourceAccessor;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.schema.TargetType;
import org.hibernate.tool.schema.spi.SchemaExport;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;

public class LiquibaseMigrationGenerator {
    public static void main(String[] args) throws Exception {
        // 1. Создать базу данных из JPA Entities через Hibernate
        String jdbcUrl = "jdbc:postgresql://localhost:5433/necpgame";
        String username = "necpgame";
        String password = "necpgame";
        
        // Создать схему из Entities
        createSchemaFromEntities(jdbcUrl, username, password);
        
        // 2. Использовать Liquibase для генерации changelog
        Database database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(
                        DriverManager.getConnection(jdbcUrl, username, password)
                );
        
        // Сравнить схему с эталонной (пустой) и создать changelog
        DiffResult diffResult = DiffGeneratorFactory.getInstance()
                .compare(database, null, new ClassLoaderResourceAccessor());
        
        // Сгенерировать changelog
        DiffToChangeLog diffToChangeLog = new DiffToChangeLog(
                diffResult,
                new DiffOutputControl(false, false, false, null)
        );
        
        String changeLogFile = "src/main/resources/db/changelog/db.changelog-master.xml";
        try (PrintWriter writer = new PrintWriter(new FileWriter(changeLogFile))) {
            diffToChangeLog.print(writer);
        }
        
        System.out.println("✅ Changelog сгенерирован: " + changeLogFile);
    }
    
    private static void createSchemaFromEntities(String jdbcUrl, String username, String password) {
        // Реализация создания схемы из JPA Entities через Hibernate SchemaExport
        // ...
    }
}
```

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

1. **Liquibase требует реальную базу данных** для генерации changelog через `diffChangeLog`
2. **Нужно сначала создать схему** из JPA Entities (через Hibernate), затем генерировать changelog
3. **Для автоматизации** можно использовать Java программу, которая создает временную базу данных из Entities, затем генерирует changelog

## 🎯 Рекомендуемый подход

**Комбинация Hibernate SchemaExport + Liquibase:**

1. Использовать Hibernate SchemaExport для создания SQL из JPA Entities
2. Применить SQL к временной базе данных
3. Использовать Liquibase `generateChangeLog` для создания changelog из базы данных
4. Использовать Liquibase для управления миграциями в продакшене

**Преимущества:**
- ✅ Используется только Liquibase для управления миграциями
- ✅ Автоматическая генерация из JPA Entities
- ✅ Поддержка версионности
- ✅ Интеграция с Spring Boot

---

## 🔗 Полезные ссылки

- [Liquibase Documentation](https://docs.liquibase.com/)
- [Liquibase Maven Plugin](https://docs.liquibase.com/tools-integrations/maven/home.html)
- [Spring Boot Liquibase Integration](https://docs.spring.io/spring-boot/docs/current/reference/html/data.html#data.sql.liquibase)

