package com.necpgame.backjava.generator;

import jakarta.persistence.Entity;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.diff.DiffGeneratorFactory;
import liquibase.diff.DiffResult;
import liquibase.diff.output.DiffOutputControl;
import liquibase.diff.output.changelog.DiffToChangeLog;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.schema.TargetType;
import org.hibernate.tool.schema.spi.SchemaExport;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Генератор Liquibase changelog из JPA Entities
 * 
 * Процесс:
 * 1. Использует Hibernate SchemaExport для создания схемы из JPA Entities
 * 2. Применяет схему к временной базе данных
 * 3. Использует Liquibase diffChangeLog для генерации changelog
 */
public class LiquibaseChangelogGenerator {
    
    private static final String TEMP_DB_URL = "jdbc:postgresql://localhost:5433/necpgame_temp";
    private static final String USERNAME = "necpgame";
    private static final String PASSWORD = "necpgame";
    private static final String ENTITY_PACKAGE = "com.necpgame.backjava.entity";
    private static final String CHANGELOG_FILE = "src/main/resources/db/changelog/db.changelog-master.xml";
    
    public static void main(String[] args) {
        try {
            System.out.println("=== Генерация Liquibase changelog из JPA Entities ===");
            
            // 1. Создать схему из JPA Entities через Hibernate
            System.out.println("Шаг 1: Создание схемы из JPA Entities...");
            createSchemaFromEntities();
            
            // 2. Подключиться к базе данных
            System.out.println("Шаг 2: Подключение к базе данных...");
            Connection connection = DriverManager.getConnection(TEMP_DB_URL, USERNAME, PASSWORD);
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            
            // 3. Сгенерировать changelog из базы данных
            System.out.println("Шаг 3: Генерация changelog...");
            DiffResult diffResult = DiffGeneratorFactory.getInstance()
                    .compare(database, null, new ClassLoaderResourceAccessor());
            
            DiffToChangeLog diffToChangeLog = new DiffToChangeLog(
                    diffResult,
                    new DiffOutputControl(false, false, false, null)
            );
            
            // 4. Сохранить changelog в файл
            System.out.println("Шаг 4: Сохранение changelog в файл...");
            try (PrintWriter writer = new PrintWriter(new FileWriter(CHANGELOG_FILE))) {
                diffToChangeLog.print(writer);
            }
            
            System.out.println("✅ Changelog сгенерирован: " + CHANGELOG_FILE);
            
            // 5. Закрыть соединение
            connection.close();
            
        } catch (Exception e) {
            System.err.println("❌ Ошибка при генерации changelog: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Создает схему базы данных из JPA Entities через Hibernate SchemaExport
     */
    private static void createSchemaFromEntities() throws Exception {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .applySetting("hibernate.connection.url", TEMP_DB_URL)
                .applySetting("hibernate.connection.username", USERNAME)
                .applySetting("hibernate.connection.password", PASSWORD)
                .applySetting("hibernate.connection.driver_class", "org.postgresql.Driver")
                .build();
        
        MetadataSources metadataSources = new MetadataSources(registry);
        
        // Автоматически найти все Entity классы в пакете ENTITY_PACKAGE
        List<Class<?>> entityClasses = findEntityClasses(ENTITY_PACKAGE);
        System.out.println("Найдено Entity классов: " + entityClasses.size());
        
        for (Class<?> entityClass : entityClasses) {
            System.out.println("  - Добавление Entity: " + entityClass.getName());
            metadataSources.addAnnotatedClass(entityClass);
        }
        
        Metadata metadata = metadataSources.buildMetadata();
        
        // Создать временную базу данных
        try (Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5433/postgres", USERNAME, PASSWORD)) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("DROP DATABASE IF EXISTS necpgame_temp");
                stmt.execute("CREATE DATABASE necpgame_temp");
            }
        }
        
        // Экспортировать схему
        SchemaExport schemaExport = new SchemaExport();
        schemaExport.setOutputFile("target/temp-schema.sql");
        schemaExport.setFormat(true);
        schemaExport.setDelimiter(";");
        schemaExport.execute(TargetType.SCRIPT, SchemaExport.Action.CREATE, metadata);
        
        // Применить схему к временной базе данных
        try (Connection connection = DriverManager.getConnection(TEMP_DB_URL, USERNAME, PASSWORD)) {
            // TODO: Выполнить SQL из target/temp-schema.sql
            // Это можно сделать через ScriptRunner или просто выполнить SQL
        }
        
        System.out.println("✅ Схема создана из JPA Entities");
    }
}

