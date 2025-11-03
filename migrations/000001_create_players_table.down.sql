-- Откат миграции
DROP INDEX IF EXISTS idx_players_class;
DROP INDEX IF EXISTS idx_players_id;
DROP TABLE IF EXISTS players;

