-- Создание таблиц для имплантов
-- Идемпотентная миграция

-- Таблица типов имплантов
CREATE TABLE IF NOT EXISTS implant_types (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Таблица имплантов
CREATE TABLE IF NOT EXISTS implants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    energy_consumption FLOAT DEFAULT 0.0 CHECK (energy_consumption >= 0),
    individual_limit FLOAT,
    compatibility_rules JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (type) REFERENCES implant_types(type)
);

-- Таблица слотов имплантов игрока
CREATE TABLE IF NOT EXISTS implant_slots (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    player_id UUID NOT NULL,
    slot_type VARCHAR(50) NOT NULL,
    slot_position INTEGER NOT NULL,
    is_occupied BOOLEAN DEFAULT FALSE,
    installed_implant_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE,
    FOREIGN KEY (installed_implant_id) REFERENCES implants(id) ON DELETE SET NULL,
    UNIQUE(player_id, slot_type, slot_position)
);

-- Таблица установленных имплантов
CREATE TABLE IF NOT EXISTS installed_implants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    player_id UUID NOT NULL,
    implant_id UUID NOT NULL,
    slot_id UUID NOT NULL,
    installed_at TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE,
    FOREIGN KEY (implant_id) REFERENCES implants(id) ON DELETE CASCADE,
    FOREIGN KEY (slot_id) REFERENCES implant_slots(id) ON DELETE CASCADE,
    UNIQUE(player_id, slot_id)
);

-- Таблица совместимостей имплантов
CREATE TABLE IF NOT EXISTS implant_compatibilities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    implant_id UUID NOT NULL,
    incompatible_implant_id UUID NOT NULL,
    conflict_severity VARCHAR(20) DEFAULT 'medium' CHECK (conflict_severity IN ('low', 'medium', 'high', 'critical')),
    reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (implant_id) REFERENCES implants(id) ON DELETE CASCADE,
    FOREIGN KEY (incompatible_implant_id) REFERENCES implants(id) ON DELETE CASCADE,
    UNIQUE(implant_id, incompatible_implant_id)
);

-- Создание индексов
CREATE INDEX IF NOT EXISTS idx_implants_type ON implants(type);
CREATE INDEX IF NOT EXISTS idx_implant_slots_player_id ON implant_slots(player_id);
CREATE INDEX IF NOT EXISTS idx_implant_slots_type ON implant_slots(slot_type);
CREATE INDEX IF NOT EXISTS idx_installed_implants_player_id ON installed_implants(player_id);
CREATE INDEX IF NOT EXISTS idx_installed_implants_implant_id ON installed_implants(implant_id);
CREATE INDEX IF NOT EXISTS idx_implant_compatibilities_implant_id ON implant_compatibilities(implant_id);

