package config

import (
	"fmt"
	"os"
)

// Config содержит конфигурацию приложения
type Config struct {
	ServerPort string
	ServerHost string
	DBHost     string
	DBPort     string
	DBUser     string
	DBPassword string
	DBName     string
	DBSSLMode  string
	LogLevel   string
}

// Load загружает конфигурацию из переменных окружения
func Load() *Config {
	return &Config{
		ServerPort: getEnv("SERVER_PORT", "8080"),
		ServerHost: getEnv("SERVER_HOST", "localhost"),
		DBHost:     getEnv("POSTGRES_HOST", "localhost"),
		DBPort:     getEnv("POSTGRES_PORT", "5432"),
		DBUser:     getEnv("POSTGRES_USER", "necpgame"),
		DBPassword: getEnv("POSTGRES_PASSWORD", "necpgame"),
		DBName:     getEnv("POSTGRES_DB", "necpgame"),
		DBSSLMode:  getEnv("POSTGRES_SSLMODE", "disable"),
		LogLevel:   getEnv("LOG_LEVEL", "info"),
	}
}

// getEnv получает значение переменной окружения или возвращает значение по умолчанию
func getEnv(key, defaultValue string) string {
	if value := os.Getenv(key); value != "" {
		return value
	}
	return defaultValue
}

// DatabaseURL возвращает строку подключения к БД
func (c *Config) DatabaseURL() string {
	return fmt.Sprintf(
		"postgres://%s:%s@%s:%s/%s?sslmode=%s",
		c.DBUser,
		c.DBPassword,
		c.DBHost,
		c.DBPort,
		c.DBName,
		c.DBSSLMode,
	)
}

