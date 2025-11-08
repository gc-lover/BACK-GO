# Шаблоны для ручного создания классов

> После генерации контрактов через OpenAPI Generator, используй эти шаблоны для создания реализации вручную в `src/main/java/`.

## 📋 Оглавление
1. [Entity Template](#entity-template)
2. [Repository Template](#repository-template)
3. [Controller Template](#controller-template)
4. [ServiceImpl Template](#serviceimpl-template)
5. [Exception Templates](#exception-templates)
6. [Mapper Template](#mapper-template)

---

## 1. Entity Template

**Путь**: `src/main/java/entity/YourEntity.java`

```java
package com.necpgame.backjava.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity для [описание сущности].
 * 
 * Связанная таблица: your_table_name
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "your_table_name",
    indexes = {
        @Index(name = "idx_field1", columnList = "field1"),
        @Index(name = "idx_field2_field3", columnList = "field2, field3")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_field1", columnNames = "field1")
    }
)
public class YourEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    // Основные поля
    @Column(name = "field1", nullable = false, unique = true, length = 100)
    private String field1;
    
    @Column(name = "field2", nullable = false)
    private String field2;
    
    @Column(name = "field3")
    private Integer field3;
    
    // Relationships
    
    // Many-to-One (владелец связи)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parent_id", nullable = false, foreignKey = @ForeignKey(name = "fk_your_entity_parent"))
    private ParentEntity parent;
    
    // One-to-Many (обратная сторона)
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChildEntity> children = new ArrayList<>();
    
    // Many-to-Many (владелец связи)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "your_entity_related",
        joinColumns = @JoinColumn(name = "your_entity_id", foreignKey = @ForeignKey(name = "fk_your_entity")),
        inverseJoinColumns = @JoinColumn(name = "related_id", foreignKey = @ForeignKey(name = "fk_related"))
    )
    private Set<RelatedEntity> relatedEntities = new HashSet<>();
    
    // Timestamps
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Soft Delete
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    // Status/Flags
    @Column(name = "active", nullable = false)
    private Boolean active = true;
    
    // Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        if (active == null) {
            active = true;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        // Дополнительная логика при обновлении
    }
    
    // Helper Methods
    public void addChild(ChildEntity child) {
        children.add(child);
        child.setParent(this);
    }
    
    public void removeChild(ChildEntity child) {
        children.remove(child);
        child.setParent(null);
    }
    
    public boolean isDeleted() {
        return deletedAt != null;
    }
    
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.active = false;
    }
}
```

---

## 2. Repository Template

**Путь**: `src/main/java/repository/YourRepository.java`

```java
package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.YourEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository для работы с {@link YourEntity}.
 * 
 * Включает:
 * - CRUD операции (через JpaRepository)
 * - Derived Query Methods
 * - Custom JPQL Queries
 * - Specification поддержка (через JpaSpecificationExecutor)
 */
@Repository
public interface YourRepository extends 
        JpaRepository<YourEntity, UUID>, 
        JpaSpecificationExecutor<YourEntity> {
    
    // ==================================================================================
    // Derived Query Methods (Spring Data автоматически создаёт реализацию)
    // ==================================================================================
    
    Optional<YourEntity> findByField1(String field1);
    
    List<YourEntity> findByField2(String field2);
    
    Page<YourEntity> findByField2AndActive(String field2, Boolean active, Pageable pageable);
    
    boolean existsByField1(String field1);
    
    boolean existsByField1AndIdNot(String field1, UUID id);
    
    long countByActive(Boolean active);
    
    List<YourEntity> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    List<YourEntity> findByField2ContainingIgnoreCase(String field2);
    
    void deleteByParentId(UUID parentId);
    
    // ==================================================================================
    // Custom JPQL Queries
    // ==================================================================================
    
    @Query("SELECT y FROM YourEntity y WHERE y.field1 = :field1 AND y.active = true")
    Optional<YourEntity> findActiveByField1(@Param("field1") String field1);
    
    @Query("SELECT y FROM YourEntity y " +
           "LEFT JOIN FETCH y.children " +
           "WHERE y.id = :id")
    Optional<YourEntity> findByIdWithChildren(@Param("id") UUID id);
    
    @Query("SELECT y FROM YourEntity y " +
           "LEFT JOIN FETCH y.parent " +
           "WHERE y.field2 = :field2 AND y.deletedAt IS NULL")
    List<YourEntity> findNotDeletedByField2WithParent(@Param("field2") String field2);
    
    @Query(value = "SELECT * FROM your_table_name y " +
                   "WHERE y.field3 > :threshold " +
                   "ORDER BY y.created_at DESC " +
                   "LIMIT :limit",
           nativeQuery = true)
    List<YourEntity> findTopByField3GreaterThan(@Param("threshold") Integer threshold, 
                                                 @Param("limit") int limit);
    
    // ==================================================================================
    // Custom Update/Delete Queries
    // ==================================================================================
    
    @Modifying
    @Query("UPDATE YourEntity y SET y.active = false WHERE y.id = :id")
    int deactivateById(@Param("id") UUID id);
    
    @Modifying
    @Query("UPDATE YourEntity y SET y.deletedAt = :deletedAt WHERE y.parent.id = :parentId")
    int softDeleteByParentId(@Param("parentId") UUID parentId, @Param("deletedAt") LocalDateTime deletedAt);
    
    @Modifying
    @Query("DELETE FROM YourEntity y WHERE y.deletedAt IS NOT NULL AND y.deletedAt < :threshold")
    int hardDeleteOldSoftDeleted(@Param("threshold") LocalDateTime threshold);
    
    // ==================================================================================
    // Aggregation Queries
    // ==================================================================================
    
    @Query("SELECT COUNT(y) FROM YourEntity y WHERE y.parent.id = :parentId AND y.active = true")
    long countActiveByParentId(@Param("parentId") UUID parentId);
    
    @Query("SELECT AVG(y.field3) FROM YourEntity y WHERE y.active = true")
    Double getAverageField3();
    
    // ==================================================================================
    // Projection Queries (DTOs)
    // ==================================================================================
    
    @Query("SELECT new com.necpgame.backjava.dto.YourEntitySummaryDto(y.id, y.field1, y.field2) " +
           "FROM YourEntity y " +
           "WHERE y.active = true")
    List<YourEntitySummaryDto> findAllActiveSummaries();
}
```

---

## 3. Controller Template

**Путь**: `src/main/java/controller/YourController.java`

```java
package com.necpgame.backjava.controller;

import com.necpgame.backjava.api.YourApi;  // Сгенерированный контракт
import com.necpgame.backjava.model.*;      // Сгенерированные DTOs
import com.necpgame.backjava.service.YourService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller для работы с [ресурсом].
 * 
 * Реализует контракт {@link YourApi}, сгенерированный из OpenAPI спецификации.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class YourController implements YourApi {
    
    private final YourService yourService;
    
    /**
     * Получить список всех элементов (с пагинацией).
     */
    @Override
    public ResponseEntity<YourListResponse> getAll(
            Integer page,
            Integer size,
            String sortBy,
            String sortDirection
    ) {
        log.info("GET /your-resource - page: {}, size: {}", page, size);
        
        YourListResponse response = yourService.getAll(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Получить элемент по ID.
     */
    @Override
    public ResponseEntity<YourDto> getById(UUID id) {
        log.info("GET /your-resource/{}", id);
        
        YourDto response = yourService.getById(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Создать новый элемент.
     */
    @Override
    public ResponseEntity<YourDto> create(@Valid YourCreateRequest request) {
        log.info("POST /your-resource - field1: {}", request.getField1());
        
        YourDto response = yourService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Обновить существующий элемент.
     */
    @Override
    public ResponseEntity<YourDto> update(UUID id, @Valid YourUpdateRequest request) {
        log.info("PUT /your-resource/{} - updating", id);
        
        YourDto response = yourService.update(id, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Частично обновить элемент.
     */
    @Override
    public ResponseEntity<YourDto> patch(UUID id, YourPatchRequest request) {
        log.info("PATCH /your-resource/{}", id);
        
        YourDto response = yourService.patch(id, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Удалить элемент.
     */
    @Override
    public ResponseEntity<Void> delete(UUID id) {
        log.info("DELETE /your-resource/{}", id);
        
        yourService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Пример custom endpoint.
     */
    @Override
    public ResponseEntity<YourStatsResponse> getStats(UUID id) {
        log.info("GET /your-resource/{}/stats", id);
        
        YourStatsResponse response = yourService.getStats(id);
        return ResponseEntity.ok(response);
    }
}
```

---

## 4. ServiceImpl Template

**Путь**: `src/main/java/service/impl/YourServiceImpl.java`

```java
package com.necpgame.backjava.service.impl;

import com.necpgame.backjava.service.YourService;  // Сгенерированный контракт
import com.necpgame.backjava.model.*;              // Сгенерированные DTOs
import com.necpgame.backjava.entity.YourEntity;
import com.necpgame.backjava.repository.YourRepository;
import com.necpgame.backjava.exception.*;
import com.necpgame.backjava.mapper.YourMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Реализация бизнес-логики для работы с [ресурсом].
 * 
 * Реализует контракт {@link YourService}, сгенерированный из OpenAPI спецификации.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class YourServiceImpl implements YourService {
    
    private final YourRepository yourRepository;
    private final YourMapper yourMapper;
    // Inject другие зависимости (repositories, services, clients)
    
    @Override
    @Transactional(readOnly = true)
    public YourListResponse getAll(Integer page, Integer size, String sortBy, String sortDirection) {
        log.debug("Getting all items - page: {}, size: {}", page, size);
        
        // Создание Pageable
        Sort sort = Sort.by(
            "DESC".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC,
            sortBy != null ? sortBy : "createdAt"
        );
        Pageable pageable = PageRequest.of(
            page != null ? page : 0,
            size != null ? size : 20,
            sort
        );
        
        // Получение данных
        Page<YourEntity> entityPage = yourRepository.findAll(pageable);
        
        // Маппинг Entity → DTO
        List<YourDto> items = entityPage.getContent().stream()
            .map(yourMapper::toDto)
            .toList();
        
        // Создание response
        YourListResponse response = new YourListResponse();
        response.setItems(items);
        response.setTotalElements(entityPage.getTotalElements());
        response.setTotalPages(entityPage.getTotalPages());
        response.setCurrentPage(entityPage.getNumber());
        response.setPageSize(entityPage.getSize());
        
        return response;
    }
    
    @Override
    @Transactional(readOnly = true)
    public YourDto getById(UUID id) {
        log.debug("Getting item by id: {}", id);
        
        YourEntity entity = yourRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Item not found: " + id));
        
        return yourMapper.toDto(entity);
    }
    
    @Override
    @Transactional
    public YourDto create(YourCreateRequest request) {
        log.info("Creating new item - field1: {}", request.getField1());
        
        // Валидация бизнес-правил
        if (yourRepository.existsByField1(request.getField1())) {
            throw new ConflictException("Item with field1 '" + request.getField1() + "' already exists");
        }
        
        // Маппинг DTO → Entity
        YourEntity entity = yourMapper.toEntity(request);
        
        // Дополнительная логика (например, установка relationships)
        // entity.setParent(parentRepository.findById(request.getParentId())
        //     .orElseThrow(() -> new NotFoundException("Parent not found")));
        
        // Сохранение
        YourEntity savedEntity = yourRepository.save(entity);
        
        log.info("Created item with id: {}", savedEntity.getId());
        
        return yourMapper.toDto(savedEntity);
    }
    
    @Override
    @Transactional
    public YourDto update(UUID id, YourUpdateRequest request) {
        log.info("Updating item: {}", id);
        
        // Получение существующей сущности
        YourEntity entity = yourRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Item not found: " + id));
        
        // Валидация бизнес-правил
        if (!entity.getField1().equals(request.getField1()) &&
            yourRepository.existsByField1AndIdNot(request.getField1(), id)) {
            throw new ConflictException("Item with field1 '" + request.getField1() + "' already exists");
        }
        
        // Обновление полей
        yourMapper.updateEntityFromDto(request, entity);
        
        // Сохранение
        YourEntity updatedEntity = yourRepository.save(entity);
        
        log.info("Updated item: {}", id);
        
        return yourMapper.toDto(updatedEntity);
    }
    
    @Override
    @Transactional
    public YourDto patch(UUID id, YourPatchRequest request) {
        log.info("Patching item: {}", id);
        
        YourEntity entity = yourRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Item not found: " + id));
        
        // Обновляем только те поля, которые переданы
        if (request.getField1() != null) {
            entity.setField1(request.getField1());
        }
        if (request.getField2() != null) {
            entity.setField2(request.getField2());
        }
        
        YourEntity updatedEntity = yourRepository.save(entity);
        
        return yourMapper.toDto(updatedEntity);
    }
    
    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Deleting item: {}", id);
        
        YourEntity entity = yourRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Item not found: " + id));
        
        // Soft delete (если реализовано)
        entity.softDelete();
        yourRepository.save(entity);
        
        // Или hard delete
        // yourRepository.delete(entity);
        
        log.info("Deleted item: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public YourStatsResponse getStats(UUID id) {
        log.debug("Getting stats for item: {}", id);
        
        YourEntity entity = yourRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Item not found: " + id));
        
        // Бизнес-логика для вычисления статистики
        YourStatsResponse response = new YourStatsResponse();
        response.setItemId(entity.getId());
        response.setChildrenCount(entity.getChildren().size());
        response.setActive(entity.getActive());
        // ... другие поля статистики
        
        return response;
    }
}
```

---

## 5. Exception Templates

**Путь**: `src/main/java/exception/YourCustomException.java`

```java
package com.necpgame.backjava.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Not Found (404)
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}

// Conflict (409)
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}

// Bad Request (400)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}

// Unauthorized (401)
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}

// Forbidden (403)
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
```

---

## 6. Mapper Template (MapStruct)

**Путь**: `src/main/java/mapper/YourMapperMS.java`

**⚠️ ВАЖНО:** Используем **MapStruct** для автоматической генерации маппинга. Не пишем маппинг вручную!

### Основной Mapper

```java
package com.necpgame.backjava.mapper;

import com.necpgame.backjava.entity.YourEntity;
import com.necpgame.backjava.model.*;  // Сгенерированные DTOs
import org.mapstruct.*;
import org.openapitools.jackson.nullable.JsonNullable;

/**
 * MapStruct Mapper для автоматической конвертации между Entity и DTO.
 * 
 * MapStruct генерирует реализацию во время компиляции.
 */
@Mapper(
    componentModel = "spring",
    uses = {JsonNullableMapper.class},  // Используем утилиты для JsonNullable
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface YourMapperMS {
    
    // Entity → DTO
    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "field1", target = "field1", qualifiedByName = "stringToJsonNullable")
    YourDto toDto(YourEntity entity);
    
    // Entity → Summary DTO
    @Mapping(source = "parent.id", target = "parentId")
    YourSummaryDto toSummaryDto(YourEntity entity);
    
    // Create Request DTO → Entity
    @Mapping(source = "field1", target = "field1", qualifiedByName = "jsonNullableToString")
    YourEntity toEntity(YourCreateRequest request);
    
    // Custom mapping для enum (если нужно)
    @Named("stringToEnum")
    default YourDto.StatusEnum stringToEnum(String status) {
        return status != null ? YourDto.StatusEnum.fromValue(status) : null;
    }
}
```

### JsonNullable Mapper (утилиты)

**Путь**: `src/main/java/mapper/JsonNullableMapper.java`

```java
package com.necpgame.backjava.mapper;

import org.mapstruct.Named;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Утилиты для работы с JsonNullable в MapStruct.
 * 
 * Сгенерированные DTOs используют JsonNullable для опциональных полей.
 */
@Component
public class JsonNullableMapper {

    @Named("stringToJsonNullable")
    public JsonNullable<String> stringToJsonNullable(String value) {
        return value != null ? JsonNullable.of(value) : JsonNullable.undefined();
    }

    @Named("jsonNullableToString")
    public String jsonNullableToString(JsonNullable<String> jsonNullable) {
        return jsonNullable != null && jsonNullable.isPresent() ? jsonNullable.get() : null;
    }

    @Named("uuidToJsonNullable")
    public JsonNullable<UUID> uuidToJsonNullable(UUID value) {
        return value != null ? JsonNullable.of(value) : JsonNullable.undefined();
    }

    @Named("jsonNullableToUuid")
    public UUID jsonNullableToUuid(JsonNullable<UUID> jsonNullable) {
        return jsonNullable != null && jsonNullable.isPresent() ? jsonNullable.get() : null;
    }

    @Named("dateToJsonNullable")
    public JsonNullable<OffsetDateTime> dateToJsonNullable(OffsetDateTime value) {
        return value != null ? JsonNullable.of(value) : JsonNullable.undefined();
    }

    @Named("jsonNullableToDate")
    public OffsetDateTime jsonNullableToDate(JsonNullable<OffsetDateTime> jsonNullable) {
        return jsonNullable != null && jsonNullable.isPresent() ? jsonNullable.get() : null;
    }
}
```

### Зависимости в pom.xml

```xml
<!-- MapStruct -->
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>

<!-- MapStruct Processor -->
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct-processor</artifactId>
    <version>1.5.5.Final</version>
    <scope>provided</scope>
</dependency>

<!-- Lombok + MapStruct -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok-mapstruct-binding</artifactId>
    <version>0.2.0</version>
    <scope>provided</scope>
</dependency>
```

### Преимущества MapStruct

✅ **Автоматическая генерация** - нет boilerplate кода  
✅ **Compile-time проверка** - ошибки на этапе компиляции  
✅ **Производительность** - нет рефлексии, быстрый код  
✅ **Поддержка JsonNullable** - через утилиты  
✅ **Простота** - только интерфейсы и аннотации

---

## 🚀 Быстрый старт

### 1. Генерируем контракты в целевой микросервис
```powershell
.\scripts\validate-openapi.ps1 -ApiSpec ../API-SWAGGER/api/v1/your-api.yaml
.\scripts\generate-openapi-microservices.ps1 -ApiDirectory ../API-SWAGGER/api/v1/
```

### 2. Создаём реализацию
```bash
# Структура директорий
mkdir -p src/main/java/com/necpgame/backjava/{entity,repository,controller,service/impl,exception,mapper}

# Копируем шаблоны и адаптируем под свой API
```

### 3. Проверяем
```bash
mvn clean compile
```

---

## 📚 Дополнительные ресурсы

- [Spring Data JPA - Query Methods](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods)
- [Hibernate Annotations Reference](https://docs.jboss.org/hibernate/orm/6.0/userguide/html_single/Hibernate_User_Guide.html#annotations)
- [MapStruct для автоматического маппинга](https://mapstruct.org/)

