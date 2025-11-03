-- Откат миграции
DROP INDEX IF EXISTS idx_implant_compatibilities_implant_id;
DROP INDEX IF EXISTS idx_installed_implants_implant_id;
DROP INDEX IF EXISTS idx_installed_implants_player_id;
DROP INDEX IF EXISTS idx_implant_slots_type;
DROP INDEX IF EXISTS idx_implant_slots_player_id;
DROP INDEX IF EXISTS idx_implants_type;

DROP TABLE IF EXISTS implant_compatibilities;
DROP TABLE IF EXISTS installed_implants;
DROP TABLE IF EXISTS implant_slots;
DROP TABLE IF EXISTS implants;
DROP TABLE IF EXISTS implant_types;
