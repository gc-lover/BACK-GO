#!/bin/bash

# Скрипт для генерации Go серверного кода из API Swagger спецификаций
# Использование: ./scripts/generate-api.sh <путь-к-api-swagger-file> <output-dir> [framework]

set -e

# Цвета для вывода
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Проверка аргументов
if [ $# -lt 2 ]; then
    echo -e "${RED}Ошибка: Недостаточно аргументов${NC}"
    echo "Использование: $0 <путь-к-api-swagger-file> <output-dir> [framework]"
    echo "Пример: $0 ../API-SWAGGER/api/v1/gameplay/social/personal-npc-tool.yaml internal/api/generated/personal-npc-tool go-gin-server"
    echo ""
    echo "Доступные фреймворки:"
    echo "  - go-server (по умолчанию)"
    echo "  - go-gin-server (Gin)"
    echo "  - go-echo-server (Echo)"
    echo "  - go-fiber-server (Fiber)"
    exit 1
fi

SWAGGER_FILE=$1
OUTPUT_DIR=$2
FRAMEWORK=${3:-go-gin-server}

# Проверка существования файла
if [ ! -f "$SWAGGER_FILE" ]; then
    echo -e "${RED}Ошибка: Файл $SWAGGER_FILE не найден${NC}"
    exit 1
fi

# Проверка установки OpenAPI Generator
if ! command -v openapi-generator-cli &> /dev/null; then
    echo -e "${YELLOW}OpenAPI Generator не установлен. Устанавливаю...${NC}"
    npm install -g @openapitools/openapi-generator-cli
fi

# Извлечение имени пакета из пути
PACKAGE_NAME=$(basename "$OUTPUT_DIR" | tr '-' '_' | tr '[:upper:]' '[:lower:]')

echo -e "${GREEN}Генерация Go серверного кода...${NC}"
echo "  Файл: $SWAGGER_FILE"
echo "  Выходная директория: $OUTPUT_DIR"
echo "  Фреймворк: $FRAMEWORK"
echo "  Пакет: $PACKAGE_NAME"
echo ""

# Создание директории, если не существует
mkdir -p "$OUTPUT_DIR"

# Генерация кода
openapi-generator-cli generate \
  -i "$SWAGGER_FILE" \
  -g "$FRAMEWORK" \
  -o "$OUTPUT_DIR" \
  --additional-properties=packageName="$PACKAGE_NAME",serverPort=8080

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Генерация завершена успешно!${NC}"
    echo ""
    echo "Следующие шаги:"
    echo "  1. Проверьте сгенерированный код в $OUTPUT_DIR"
    echo "  2. Создайте handlers в internal/handlers/"
    echo "  3. Создайте services в internal/services/"
    echo "  4. Создайте repositories в internal/repositories/"
else
    echo -e "${RED}✗ Ошибка при генерации кода${NC}"
    exit 1
fi

