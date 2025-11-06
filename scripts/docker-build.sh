#!/bin/bash
# Скрипт для сборки Docker образа бекенда
# Использование: ./scripts/docker-build.sh [tag] [--no-cache]

set -e  # Выход при ошибке

TAG="${1:-latest}"
NO_CACHE=""

# Проверка флага --no-cache
if [[ "$2" == "--no-cache" ]] || [[ "$1" == "--no-cache" ]]; then
    NO_CACHE="--no-cache"
    if [[ "$1" == "--no-cache" ]]; then
        TAG="latest"
    fi
fi

echo "🐳 Сборка Docker образа для NECPGAME Backend..."

# Проверка существования Dockerfile
if [ ! -f "Dockerfile" ]; then
    echo "❌ Dockerfile не найден!"
    exit 1
fi

# Проверка существования API-SWAGGER
if [ ! -d "../API-SWAGGER" ]; then
    echo "⚠️  Директория API-SWAGGER не найдена. Генерация OpenAPI кода будет пропущена."
fi

# Параметры сборки
IMAGE_NAME="necpgame-backend"
FULL_TAG="${IMAGE_NAME}:${TAG}"

echo "📦 Сборка образа: $FULL_TAG"

# Выполнение сборки
echo ""
echo "🔨 Начало сборки..."
docker build $NO_CACHE -t "$FULL_TAG" .

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Образ успешно собран: $FULL_TAG"
    
    # Показываем информацию об образе
    echo ""
    echo "📊 Информация об образе:"
    docker images "$IMAGE_NAME" --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}\t{{.CreatedAt}}"
    
    echo ""
    echo "💡 Для запуска используйте:"
    echo "   docker-compose up -d"
    echo "   или"
    echo "   docker run -p 8080:8080 $FULL_TAG"
else
    echo ""
    echo "❌ Ошибка при сборке образа!"
    exit 1
fi










