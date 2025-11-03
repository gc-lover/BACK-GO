# Makefile для бекенда на Go
# Упрощает работу с проектом

.PHONY: help build run test lint format clean docker-up docker-down migrate-up migrate-down

help: ## Показать справку
	@echo "Доступные команды:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  %-15s %s\n", $$1, $$2}'

build: ## Собрать проект
	go build -o bin/server ./cmd/server

run: ## Запустить сервер
	go run ./cmd/server

test: ## Запустить тесты
	go test -v -cover ./...

test-coverage: ## Запустить тесты с проверкой покрытия
	go test -v -coverprofile=coverage.out ./...
	go tool cover -html=coverage.out -o coverage.html

lint: ## Запустить линтер
	golangci-lint run

format: ## Форматировать код
	go fmt ./...
	goimports -w .

clean: ## Очистить собранные файлы
	rm -rf bin/
	rm -f coverage.out coverage.html

docker-up: ## Запустить PostgreSQL в Docker
	docker-compose up -d

docker-down: ## Остановить PostgreSQL в Docker
	docker-compose down

migrate-up: ## Применить миграции БД
	migrate -path migrations -database "postgres://necpgame:necpgame@localhost:5432/necpgame?sslmode=disable" up

migrate-down: ## Откатить миграции БД
	migrate -path migrations -database "postgres://necpgame:necpgame@localhost:5432/necpgame?sslmode=disable" down

generate-api: ## Генерация API кода (пример)
	@echo "Используй: ./scripts/generate-api.sh <swagger-file> <output-dir> [framework]"

