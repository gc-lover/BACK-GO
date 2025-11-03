package database

import (
	"context"
	"fmt"

	"necpgame/back-go/internal/config"

	"github.com/jackc/pgx/v5/pgxpool"
	"go.uber.org/zap"
)

// DB представляет пул соединений с БД
type DB struct {
	Pool *pgxpool.Pool
}

// New создает новое подключение к БД
func New(cfg *config.Config, logger *zap.Logger) (*DB, error) {
	dsn := cfg.DatabaseURL()
	
	poolConfig, err := pgxpool.ParseConfig(dsn)
	if err != nil {
		return nil, fmt.Errorf("failed to parse database URL: %w", err)
	}

	pool, err := pgxpool.NewWithConfig(context.Background(), poolConfig)
	if err != nil {
		return nil, fmt.Errorf("failed to create connection pool: %w", err)
	}

	// Проверка подключения
	if err := pool.Ping(context.Background()); err != nil {
		return nil, fmt.Errorf("failed to ping database: %w", err)
	}

	logger.Info("Database connection established",
		zap.String("host", cfg.DBHost),
		zap.String("port", cfg.DBPort),
		zap.String("database", cfg.DBName),
	)

	return &DB{
		Pool: pool,
	}, nil
}

// Close закрывает пул соединений
func (db *DB) Close() {
	if db.Pool != nil {
		db.Pool.Close()
	}
}

