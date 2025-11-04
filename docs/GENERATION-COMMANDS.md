# Команды генерации кода из OpenAPI

Этот файл содержит команды для генерации всех слоёв MVC из OpenAPI спецификаций через CLI.

---

## Установка OpenAPI Generator CLI

### Через npm (рекомендуется):
```bash
npm install -g @openapitools/openapi-generator-cli
```

### Или через Docker:
```bash
docker pull openapitools/openapi-generator-cli
```

---

## Генерация всех слоёв из одного OpenAPI файла

### 1. Генерация DTOs и API Interfaces

```bash
openapi-generator-cli generate \
  -i ../API-SWAGGER/api/v1/auth/character-creation.yaml \
  -g spring \
  -o target/generated-sources/openapi \
  --api-package com.necpgame.backjava.api \
  --model-package com.necpgame.backjava.model \
  --invoker-package com.necpgame.backjava.invoker \
  --additional-properties=\
java8=false,\
dateLibrary=java8,\
library=spring-boot,\
interfaceOnly=true,\
useSpringBoot3=true,\
useJakartaEe=true,\
openApiNullable=false,\
useBeanValidation=true,\
performBeanValidation=true
```

### 2. Генерация JPA Entities

```bash
openapi-generator-cli generate \
  -i ../API-SWAGGER/api/v1/auth/character-creation.yaml \
  -g spring \
  -t templates \
  -o target/generated-sources/entities \
  --model-package com.necpgame.backjava.entity \
  --additional-properties=\
java8=false,\
dateLibrary=java8,\
library=spring-boot,\
generateApis=false,\
generateModels=true,\
useSpringBoot3=true,\
useJakartaEe=true,\
openApiNullable=false,\
useBeanValidation=true,\
modelTemplateFiles=model.mustache=Entity.java
```

### 3. Генерация Repositories

```bash
openapi-generator-cli generate \
  -i ../API-SWAGGER/api/v1/auth/character-creation.yaml \
  -g spring \
  -t templates \
  -o target/generated-sources/repositories \
  --model-package com.necpgame.backjava.repository \
  --additional-properties=\
java8=false,\
dateLibrary=java8,\
library=spring-boot,\
generateApis=false,\
generateModels=true,\
useSpringBoot3=true,\
useJakartaEe=true,\
openApiNullable=false,\
modelTemplateFiles=Repository.mustache=Repository.java
```

### 4. Генерация Service интерфейсов

```bash
openapi-generator-cli generate \
  -i ../API-SWAGGER/api/v1/auth/character-creation.yaml \
  -g spring \
  -t templates \
  -o target/generated-sources/services \
  --api-package com.necpgame.backjava.service \
  --model-package com.necpgame.backjava.model \
  --additional-properties=\
java8=false,\
dateLibrary=java8,\
library=spring-boot,\
interfaceOnly=true,\
generateApis=true,\
generateModels=false,\
useSpringBoot3=true,\
useJakartaEe=true,\
openApiNullable=false,\
apiTemplateFiles=Service.mustache=Service.java
```

### 5. Генерация ServiceImpl реализаций

```bash
openapi-generator-cli generate \
  -i ../API-SWAGGER/api/v1/auth/character-creation.yaml \
  -g spring \
  -t templates \
  -o target/generated-sources/services \
  --api-package com.necpgame.backjava.service.impl \
  --model-package com.necpgame.backjava.model \
  --additional-properties=\
java8=false,\
dateLibrary=java8,\
library=spring-boot,\
interfaceOnly=false,\
generateApis=true,\
generateModels=false,\
useSpringBoot3=true,\
useJakartaEe=true,\
openApiNullable=false,\
apiTemplateFiles=ServiceImpl.mustache=ServiceImpl.java
```

### 6. Генерация Controllers

```bash
openapi-generator-cli generate \
  -i ../API-SWAGGER/api/v1/auth/character-creation.yaml \
  -g spring \
  -t templates \
  -o target/generated-sources/controllers \
  --api-package com.necpgame.backjava.controller \
  --model-package com.necpgame.backjava.model \
  --additional-properties=\
java8=false,\
dateLibrary=java8,\
library=spring-boot,\
interfaceOnly=false,\
generateApis=true,\
generateModels=false,\
useSpringBoot3=true,\
useJakartaEe=true,\
openApiNullable=false,\
apiTemplateFiles=apiController.mustache=Controller.java
```

---

## Генерация Liquibase Changelog

После генерации Entities, скомпилируй проект и сгенерируй changelog:

```bash
# Компиляция проекта (включая сгенерированные Entities)
mvn clean compile

# Генерация Liquibase changelog из JPA Entities
mvn liquibase:diffChangeLog
```

---

## Генерация из всех файлов в директории

Для генерации из всех OpenAPI файлов в директории, выполни команды выше для каждого файла:

```bash
# Пример: генерация из всех файлов в API-SWAGGER/api/v1/
for file in ../API-SWAGGER/api/v1/**/*.yaml; do
  echo "Генерация из $file"
  # Выполнить все 6 команд выше для каждого файла
done
```

---

## Упрощённая генерация (все слои одной командой)

Можно использовать bash-скрипт для автоматизации:

```bash
#!/bin/bash
OPENAPI_FILE=$1

if [ -z "$OPENAPI_FILE" ]; then
  echo "Использование: ./generate.sh <путь-к-openapi-файлу>"
  exit 1
fi

echo "Генерация из $OPENAPI_FILE..."

# 1. DTOs и API Interfaces
openapi-generator-cli generate -i "$OPENAPI_FILE" -g spring -o target/generated-sources/openapi --api-package com.necpgame.backjava.api --model-package com.necpgame.backjava.model --additional-properties=java8=false,dateLibrary=java8,library=spring-boot,interfaceOnly=true,useSpringBoot3=true,useJakartaEe=true

# 2. Entities
openapi-generator-cli generate -i "$OPENAPI_FILE" -g spring -t templates -o target/generated-sources/entities --model-package com.necpgame.backjava.entity --additional-properties=java8=false,generateApis=false,generateModels=true,useSpringBoot3=true,modelTemplateFiles=model.mustache=Entity.java

# 3. Repositories
openapi-generator-cli generate -i "$OPENAPI_FILE" -g spring -t templates -o target/generated-sources/repositories --model-package com.necpgame.backjava.repository --additional-properties=java8=false,generateApis=false,generateModels=true,useSpringBoot3=true,modelTemplateFiles=Repository.mustache=Repository.java

# 4. Services
openapi-generator-cli generate -i "$OPENAPI_FILE" -g spring -t templates -o target/generated-sources/services --api-package com.necpgame.backjava.service --model-package com.necpgame.backjava.model --additional-properties=java8=false,interfaceOnly=true,generateApis=true,generateModels=false,useSpringBoot3=true,apiTemplateFiles=Service.mustache=Service.java

# 5. ServiceImpl
openapi-generator-cli generate -i "$OPENAPI_FILE" -g spring -t templates -o target/generated-sources/services --api-package com.necpgame.backjava.service.impl --model-package com.necpgame.backjava.model --additional-properties=java8=false,interfaceOnly=false,generateApis=true,generateModels=false,useSpringBoot3=true,apiTemplateFiles=ServiceImpl.mustache=ServiceImpl.java

# 6. Controllers
openapi-generator-cli generate -i "$OPENAPI_FILE" -g spring -t templates -o target/generated-sources/controllers --api-package com.necpgame.backjava.controller --model-package com.necpgame.backjava.model --additional-properties=java8=false,interfaceOnly=false,generateApis=true,generateModels=false,useSpringBoot3=true,apiTemplateFiles=apiController.mustache=Controller.java

echo "Генерация завершена!"
echo "Теперь выполни: mvn clean compile && mvn liquibase:diffChangeLog"
```

Сохрани как `generate.sh` и используй:
```bash
chmod +x generate.sh
./generate.sh ../API-SWAGGER/api/v1/auth/character-creation.yaml
```

---

## Структура сгенерированных файлов

```
target/generated-sources/
├── openapi/src/main/java/
│   ├── com/necpgame/backjava/api/          # API Interfaces
│   └── com/necpgame/backjava/model/        # DTOs
├── entities/src/main/java/
│   └── com/necpgame/backjava/entity/       # JPA Entities
├── repositories/src/main/java/
│   └── com/necpgame/backjava/repository/   # Spring Data Repositories
├── services/src/main/java/
│   ├── com/necpgame/backjava/service/      # Service Interfaces
│   └── com/necpgame/backjava/service/impl/ # Service Implementations
└── controllers/src/main/java/
    └── com/necpgame/backjava/controller/   # Controller Implementations
```

---

## Примечания

- Все команды выполняются из директории `BACK-GO/`
- Шаблоны находятся в `BACK-GO/templates/`
- OpenAPI файлы находятся в `API-SWAGGER/api/v1/`
- Сгенерированные файлы автоматически добавляются в classpath через build-helper-maven-plugin

---

