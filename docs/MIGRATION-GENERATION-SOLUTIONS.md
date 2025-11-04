# Решения для генерации Flyway миграций из OpenAPI

## 📋 Обзор

Документ содержит найденные решения для автоматической генерации Flyway миграций из OpenAPI спецификации через Java.

## 🔍 Найденные решения

### ✅ Решение 1: Hibernate SchemaExport через Java программу (РЕКОМЕНДУЕТСЯ)

**Подход:** Создать Java программу, которая использует Hibernate `SchemaExport` для генерации SQL из JPA Entities.

**Преимущества:**
- ✅ Использует стандартные инструменты Hibernate
- ✅ Автоматически генерирует правильный SQL
- ✅ Поддерживает все типы данных и связи
- ✅ Не требует парсинга файлов

**Требования:**
- Entities должны содержать JPA аннотации (@Entity, @Table, @Column)
- Нужно исправить шаблон Entity.mustache, чтобы он генерировал правильные аннотации

**Реализация:**

1. Создать Java программу `MigrationGenerator.java`:

```java
package com.necpgame.backjava.generator;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.schema.TargetType;
import org.hibernate.tool.schema.spi.SchemaExport;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MigrationGenerator {
    public static void main(String[] args) {
        String entityPackage = "com.necpgame.backjava.entity";
        String outputDir = args.length > 0 ? args[0] : "src/main/resources/db/migration";
        
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .build();
        
        MetadataSources metadataSources = new MetadataSources(registry);
        
        // Найти все Entity классы в пакете
        List<Class<?>> entityClasses = findEntityClasses(entityPackage);
        
        for (Class<?> entityClass : entityClasses) {
            metadataSources.addAnnotatedClass(entityClass);
        }
        
        Metadata metadata = metadataSources.buildMetadata();
        
        SchemaExport schemaExport = new SchemaExport();
        schemaExport.setOutputFile(outputDir + "/V001__Create_schema.sql");
        schemaExport.setFormat(true);
        schemaExport.setDelimiter(";");
        schemaExport.execute(TargetType.SCRIPT, SchemaExport.Action.CREATE, metadata);
        
        System.out.println("✅ Миграции сгенерированы в: " + outputDir);
    }
    
    private static List<Class<?>> findEntityClasses(String packageName) {
        // Реализация поиска Entity классов
        // Можно использовать Reflections библиотеку или сканирование классов
        List<Class<?>> classes = new ArrayList<>();
        // TODO: Добавить логику поиска классов
        return classes;
    }
}
```

2. Добавить в `pom.xml` execution для запуска программы:

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>generate-migrations</id>
            <phase>generate-sources</phase>
            <goals>
                <goal>java</goal>
            </goals>
            <configuration>
                <mainClass>com.necpgame.backjava.generator.MigrationGenerator</mainClass>
                <arguments>
                    <argument>src/main/resources/db/migration</argument>
                </arguments>
            </configuration>
        </execution>
    </executions>
</plugin>
```

**Проблема:** Hibernate SchemaExport генерирует DDL без `IF NOT EXISTS`, нужно добавить обработку для идемпотентности.

---

### ✅ Решение 2: Использование Spring Boot hbm2ddl.auto (только для разработки)

**Подход:** Использовать Spring Boot с `hibernate.hbm2ddl.auto=update` для автоматической генерации схемы.

**Преимущества:**
- ✅ Простая настройка
- ✅ Автоматическая генерация схемы

**Недостатки:**
- ❌ Не подходит для продакшена
- ❌ Не создает версионные миграции Flyway
- ❌ Только для разработки

**Реализация:**

В `application.yml`:
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update  # Только для разработки!
```

**⚠️ ВАЖНО:** Не использовать в продакшене! Нужно использовать Flyway для управления миграциями.

---

### ✅ Решение 3: Использование Liquibase с генерацией из JPA Entities

**Подход:** Использовать Liquibase для генерации changelog из JPA Entities.

**Преимущества:**
- ✅ Поддержка версионности
- ✅ Генерация из JPA Entities

**Недостатки:**
- ❌ Требует дополнительную зависимость
- ❌ Более сложная настройка

**Реализация:**

1. Добавить зависимость Liquibase:
```xml
<dependency>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-core</artifactId>
</dependency>
```

2. Использовать Liquibase Maven plugin для генерации changelog из JPA Entities.

---

### ✅ Решение 4: Исправление шаблона Entity.mustache (текущий подход)

**Подход:** Исправить шаблон `Entity.mustache`, чтобы он генерировал правильные JPA аннотации, затем использовать Hibernate SchemaExport.

**Проблема:** Сейчас шаблон не применяется правильно - Entities генерируются без JPA аннотаций.

**Решение:** Проверить конфигурацию OpenAPI Generator для применения шаблона `Entity.mustache`.

---

## 🎯 Рекомендуемый подход

**Комбинация Решения 1 и Решения 4:**

1. Исправить шаблон `Entity.mustache`, чтобы он генерировал правильные JPA аннотации
2. Создать Java программу с Hibernate SchemaExport для генерации SQL
3. Добавить обработку SQL для идемпотентности (добавить `IF NOT EXISTS`)

**Преимущества:**
- ✅ Использует стандартные инструменты
- ✅ Автоматическая генерация из JPA Entities
- ✅ Поддержка всех типов данных и связей
- ✅ Версионность через Flyway

---

## 📝 Следующие шаги

1. Исправить шаблон `Entity.mustache` для правильной генерации JPA аннотаций
2. Создать Java программу `MigrationGenerator.java` с Hibernate SchemaExport
3. Добавить обработку SQL для идемпотентности
4. Интегрировать в процесс сборки Maven

---

## 🔗 Полезные ссылки

- [Hibernate SchemaExport Documentation](https://docs.jboss.org/hibernate/orm/6.0/javadocs/org/hibernate/tool/schema/spi/SchemaExport.html)
- [Spring Boot JPA Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/data.html#data.sql.jpa-and-spring-data)
- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Liquibase Documentation](https://docs.liquibase.com/)

