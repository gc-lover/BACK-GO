-- Создание таблицы игроков (если еще не существует)
-- Идемпотентная миграция
CREATE TABLE IF NOT EXISTS players (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    class VARCHAR(50),
    humanity_level FLOAT DEFAULT 100.0 CHECK (humanity_level >= 0 AND humanity_level <= 100),
    progression_level INTEGER DEFAULT 1 CHECK (progression_level >= 1),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Создание индексов
CREATE INDEX IF NOT EXISTS idx_players_id ON players(id);
CREATE INDEX IF NOT EXISTS idx_players_class ON players(class);

