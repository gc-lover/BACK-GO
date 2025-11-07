import json
import math
import re
from datetime import datetime
from pathlib import Path
from typing import Dict, List, Optional


BASE_DIR = Path(__file__).resolve().parents[1]
DATA_DIR = BASE_DIR.parent / ".BRAIN" / "05-technical" / "mvp-data-json"
OUTPUT_FILE = BASE_DIR / "src" / "main" / "resources" / "db" / "changelog" / "data" / "quest_catalog_entries.sql"


def read_json(path: Path):
    text = path.read_text(encoding="utf-8")
    match = re.search(r"\{", text)
    if not match:
        raise ValueError(f"No JSON object found in {path}")
    json_text = text[match.start():]
    return json.loads(json_text)


def load_reference_map(filename: str, id_field: str, name_field: str):
    file_path = DATA_DIR / filename
    if not file_path.exists():
        return {}
    data = read_json(file_path)
    items = data.get(filename.split('.')[0], [])
    return {item[id_field]: item.get(name_field) for item in items if id_field in item}


NPC_MAP = load_reference_map("npcs.json", "id", "name")
LOCATION_MAP = load_reference_map("locations.json", "id", "name")


def normalize_type(value: Optional[str]) -> str:
    if not value:
        return "SIDE"
    mapping = {
        "main": "MAIN",
        "main_story": "MAIN",
        "story": "MAIN",
        "side": "SIDE",
        "faction": "FACTION",
        "daily": "DAILY",
        "weekly": "WEEKLY",
        "random_event": "RANDOM_EVENT",
        "event": "RANDOM_EVENT"
    }
    return mapping.get(value.lower(), "SIDE")


def normalize_difficulty(quest: Dict) -> str:
    raw = quest.get("difficulty")
    if isinstance(raw, str):
        normalized = raw.strip().lower().replace(" ", "_")
        mapping = {
            "easy": "EASY",
            "normal": "MEDIUM",
            "medium": "MEDIUM",
            "moderate": "MEDIUM",
            "hard": "HARD",
            "very_hard": "VERY_HARD",
            "extreme": "EXTREME",
            "legendary": "EXTREME",
            "story": "EASY",
            "insane": "EXTREME"
        }
        return mapping.get(normalized, "MEDIUM")
    level_info = quest.get("level")
    max_level = None
    if isinstance(level_info, dict):
        max_level = level_info.get("max") or level_info.get("recommended")
    elif isinstance(level_info, (int, float)):
        max_level = level_info
    if max_level is None:
        max_level = quest.get("recommendedLevel")
    if max_level is None:
        return "MEDIUM"
    try:
        value = float(max_level)
    except (TypeError, ValueError):
        return "MEDIUM"
    if value <= 5:
        return "EASY"
    if value <= 15:
        return "MEDIUM"
    if value <= 25:
        return "HARD"
    if value <= 40:
        return "VERY_HARD"
    return "EXTREME"


def parse_duration(value: Optional[str]) -> int:
    if not value:
        return 45
    numbers = [int(n) for n in re.findall(r"\d+", value)]
    if not numbers:
        return 45
    if len(numbers) == 1:
        return numbers[0]
    return int(sum(numbers) / len(numbers))


def extract_objectives(quest: Dict) -> List[Dict[str, object]]:
    result: List[Dict[str, object]] = []
    for obj in quest.get("objectives", []) or []:
        if isinstance(obj, dict):
            result.append({
                "objective_id": obj.get("id"),
                "description": obj.get("description"),
                "optional": obj.get("optional", False)
            })
        else:
            text = str(obj)
            result.append({
                "objective_id": text,
                "description": text,
                "optional": False
            })
    return result


def gather_npcs(quest: Dict) -> List[Dict[str, Optional[str]]]:
    npc_ids = set()
    giver = quest.get("giverNpcId")
    if giver:
        npc_ids.add(giver)
    for obj in quest.get("objectives", []) or []:
        if not isinstance(obj, dict):
            continue
        target = obj.get("target") or {}
        if target.get("type") == "npc" and target.get("id"):
            npc_ids.add(target["id"])
    npcs: List[Dict[str, Optional[str]]] = []
    for npc_id in sorted(npc_ids):
        npcs.append({
            "npc_id": npc_id,
            "name": NPC_MAP.get(npc_id, npc_id),
            "role": None
        })
    return npcs


def gather_locations(quest: Dict) -> List[str]:
    locations: set[str] = set()
    for obj in quest.get("objectives", []) or []:
        if not isinstance(obj, dict):
            continue
        target = obj.get("target") or {}
        if target.get("type") == "location" and target.get("id"):
            loc_id = target["id"]
            locations.add(LOCATION_MAP.get(loc_id, loc_id))
    for loc in quest.get("locations", []) or []:
        if isinstance(loc, str):
            locations.add(LOCATION_MAP.get(loc, loc))
    return sorted(locations)


def count_skill_checks(quest: Dict) -> bool:
    for obj in quest.get("objectives", []) or []:
        if isinstance(obj, dict) and obj.get("skillChecks"):
            return True
    dialogue = quest.get("dialogueTree") or {}
    for node in dialogue.get("nodes", []) or []:
        for choice in node.get("choices", []) or []:
            if choice.get("skillCheck"):
                return True
    return False


def has_combat_flag(quest: Dict) -> bool:
    tags = {t.lower() for t in quest.get("tags", []) or []}
    if "combat" in tags:
        return True
    for obj in quest.get("objectives", []) or []:
        if not isinstance(obj, dict):
            continue
        for check in obj.get("skillChecks", []) or []:
            fail = (check.get("failEffect") or {}).get("type")
            if isinstance(fail, str) and "combat" in fail.lower():
                return True
    return False


def extract_dialogue_tree(quest: Dict) -> Optional[Dict[str, object]]:
    tree = quest.get("dialogueTree")
    if not tree:
        return None
    nodes = []
    for node in tree.get("nodes", []) or []:
        choices = []
        for choice in node.get("choices", []) or []:
            choices.append({
                "choice_id": choice.get("id"),
                "text": choice.get("text"),
                "leads_to_node": choice.get("nextNode"),
                "skill_check": choice.get("skillCheck")
            })
        nodes.append({
            "node_id": node.get("id"),
            "speaker": node.get("speaker"),
            "text": node.get("text"),
            "choices": choices
        })
    return {
        "quest_id": quest.get("questId") or quest.get("id"),
        "root_node_id": tree.get("rootNode") or tree.get("root"),
        "total_nodes": len(nodes),
        "nodes": nodes
    }


def extract_loot_table(quest: Dict) -> Optional[Dict[str, object]]:
    loot = quest.get("lootTables") or quest.get("lootTable")
    if not loot:
        return None
    guaranteed = []
    random_loot = []
    if isinstance(loot, dict):
        guaranteed = loot.get("guaranteedLoot") or loot.get("guaranteed") or []
        random_loot = loot.get("randomLoot") or loot.get("random") or []
    elif isinstance(loot, list):
        for entry in loot:
            if isinstance(entry, dict):
                if entry.get("type") == "guaranteed":
                    guaranteed.extend(entry.get("items", []))
                elif entry.get("type") == "random":
                    random_loot.extend(entry.get("items", []))
            else:
                guaranteed.append(entry)
    format_guaranteed = []
    for item in guaranteed:
        if isinstance(item, dict):
            format_guaranteed.append({
                "item_id": item.get("itemId") or item.get("id"),
                "quantity": item.get("quantity", 1)
            })
        else:
            format_guaranteed.append({"item_id": item, "quantity": 1})
    format_random = []
    for item in random_loot:
        if isinstance(item, dict):
            quantity = item.get("quantityRange") or item.get("quantity")
            if isinstance(quantity, dict):
                quantity_range = {
                    "min": quantity.get("min", 1),
                    "max": quantity.get("max", quantity.get("min", 1))
                }
            else:
                quantity_range = {"min": 1, "max": quantity or 1}
            format_random.append({
                "item_id": item.get("itemId") or item.get("id"),
                "drop_chance": item.get("chance") or item.get("dropChance") or 0,
                "quantity_range": quantity_range
            })
        else:
            format_random.append({
                "item_id": item,
                "drop_chance": 0,
                "quantity_range": {"min": 1, "max": 1}
            })
    return {
        "quest_id": quest.get("questId") or quest.get("id"),
        "guaranteed_loot": format_guaranteed,
        "random_loot": format_random
    }


def compute_summary_rewards(quest: Dict) -> Dict[str, int]:
    rewards = quest.get("rewards") or {}
    experience = rewards.get("experience") or rewards.get("xp") or 0
    eddies = rewards.get("money") or rewards.get("eddies") or 0
    items = rewards.get("items")
    if isinstance(items, dict):
        items_count = sum(items.values())
    elif isinstance(items, list):
        items_count = len(items)
    else:
        items_count = 0
    return {
        "experience": experience or 0,
        "eddies": eddies or 0,
        "items_count": items_count
    }


def build_entry(period: str, quest: Dict) -> Dict[str, object]:
    quest_id = quest.get("questId") or quest.get("id")
    level_info = quest.get("level")
    level_min = None
    level_max = None
    if isinstance(level_info, dict):
        level_min = level_info.get("min")
        level_max = level_info.get("max")
    elif isinstance(level_info, (int, float)):
        level_min = level_max = level_info
    match_score_base = len(quest.get("tags", []) or [])
    completion_rate = min(95, 35 + match_score_base * 7)
    average_rating = min(95, 60 + match_score_base * 4) / 20
    synopsis = quest.get("synopsis") or quest.get("description")
    entry: Dict[str, object] = {
        "quest_id": quest_id,
        "title": quest.get("name") or quest_id,
        "description": synopsis[:512] if isinstance(synopsis, str) else synopsis,
        "type": normalize_type(quest.get("type")),
        "period": period,
        "difficulty": normalize_difficulty(quest),
        "level_requirement": level_min or 1,
        "level_cap": level_max,
        "faction": quest.get("faction"),
        "estimated_time_minutes": parse_duration(quest.get("duration")),
        "tags": quest.get("tags", []),
        "rewards_summary": compute_summary_rewards(quest),
        "completion_rate": round(completion_rate / 100, 4),
        "average_rating": round(average_rating, 2),
        "full_description": quest.get("longDescription") or quest.get("narrative") or synopsis,
        "storyline": quest.get("storyline") or quest.get("arc") or quest.get("chain"),
        "objectives": extract_objectives(quest),
        "key_npcs": gather_npcs(quest),
        "locations": gather_locations(quest),
        "prerequisites": quest.get("prerequisites") or quest.get("requirements", {}).get("completedQuests") or [],
        "unlocks": quest.get("unlocks") or [],
        "branches_count": len(quest.get("branches", []) or []),
        "endings_count": len(quest.get("endings", []) or []),
        "has_dialogue_tree": bool(quest.get("dialogueTree")),
        "has_skill_checks": count_skill_checks(quest),
        "has_combat": has_combat_flag(quest),
        "has_romance": "romance" in [t.lower() for t in quest.get("tags", []) or []],
        "rewards_detailed": quest.get("rewards") or {},
        "dialogue_tree": extract_dialogue_tree(quest),
        "loot_table": extract_loot_table(quest),
        "metadata": {
            "source": quest.get("source") or quest.get("origin"),
            "repeatable": quest.get("repeatable"),
            "period_source": period
        }
    }
    return entry


def collect_entries() -> Dict[str, Dict[str, object]]:
    entries: Dict[str, Dict[str, object]] = {}
    quest_files = sorted(DATA_DIR.glob("quests*.json"))
    for file_path in quest_files:
        if "unique-starts" in file_path.name:
            continue
        try:
            data = read_json(file_path)
        except Exception as exc:  # pylint: disable=broad-except
            print(f"Skip {file_path.name}: {exc}")
            continue

        period = data.get("period") or data.get("timeline") or file_path.stem.replace("quests-", "")
        if isinstance(period, dict):
            period = period.get("name") or period.get("range")

        quest_lists: List[List[Dict]] = []
        for key, value in data.items():
            if isinstance(value, list) and key.lower().startswith("quests"):
                quest_lists.append(value)
        if not quest_lists:
            continue

        for quest_list in quest_lists:
            for quest in quest_list:
                try:
                    entry = build_entry(period, quest)
                except Exception as exc:  # pylint: disable=broad-except
                    quest_id = quest.get("questId") or quest.get("id") or "unknown"
                    print(f"Skip quest {quest_id} from {file_path.name}: {exc}")
                    continue
                entries[entry["quest_id"]] = entry
    return entries


def sql_value(value):
    if value is None:
        return "NULL"
    if isinstance(value, bool):
        return "TRUE" if value else "FALSE"
    if isinstance(value, (int, float)):
        if isinstance(value, float) and (math.isnan(value) or math.isinf(value)):
            return "NULL"
        return str(value)
    if isinstance(value, (list, dict)):
        return f"'{json.dumps(value, ensure_ascii=False)}'::jsonb"
    text = str(value)
    text = text.replace("'", "''")
    return f"'{text}'"


def compose_sql(entries: Dict[str, Dict[str, object]]) -> str:
    statements = ["-- Generated on " + datetime.utcnow().isoformat(timespec="seconds") + "Z"]
    columns = [
        "quest_id", "title", "description", "type", "period", "difficulty",
        "level_requirement", "level_cap", "faction", "estimated_time_minutes",
        "tags_json", "rewards_summary_json", "completion_rate", "average_rating",
        "full_description", "storyline", "objectives_json", "key_npcs_json",
        "locations_json", "prerequisites_json", "unlocks_json", "branches_count",
        "endings_count", "has_dialogue_tree", "has_skill_checks", "has_combat",
        "has_romance", "rewards_detailed_json", "dialogue_tree_json", "loot_table_json",
        "metadata_json"
    ]
    for entry in entries.values():
        values = [
            sql_value(entry["quest_id"]),
            sql_value(entry["title"]),
            sql_value(entry.get("description")),
            sql_value(entry.get("type")),
            sql_value(entry.get("period")),
            sql_value(entry.get("difficulty")),
            sql_value(entry.get("level_requirement")),
            sql_value(entry.get("level_cap")),
            sql_value(entry.get("faction")),
            sql_value(entry.get("estimated_time_minutes")),
            sql_value(entry.get("tags")),
            sql_value(entry.get("rewards_summary")),
            sql_value(entry.get("completion_rate")),
            sql_value(entry.get("average_rating")),
            sql_value(entry.get("full_description")),
            sql_value(entry.get("storyline")),
            sql_value(entry.get("objectives")),
            sql_value(entry.get("key_npcs")),
            sql_value(entry.get("locations")),
            sql_value(entry.get("prerequisites")),
            sql_value(entry.get("unlocks")),
            sql_value(entry.get("branches_count")),
            sql_value(entry.get("endings_count")),
            sql_value(entry.get("has_dialogue_tree")),
            sql_value(entry.get("has_skill_checks")),
            sql_value(entry.get("has_combat")),
            sql_value(entry.get("has_romance")),
            sql_value(entry.get("rewards_detailed")),
            sql_value(entry.get("dialogue_tree")),
            sql_value(entry.get("loot_table")),
            sql_value(entry.get("metadata"))
        ]
        insert_stmt = (
            "INSERT INTO quest_catalog_entries (" + ", ".join(columns) + ") VALUES (" + ", ".join(values) + ") "
            "ON CONFLICT (quest_id) DO UPDATE SET "
            "title = EXCLUDED.title, description = EXCLUDED.description, type = EXCLUDED.type, "
            "period = EXCLUDED.period, difficulty = EXCLUDED.difficulty, level_requirement = EXCLUDED.level_requirement, "
            "level_cap = EXCLUDED.level_cap, faction = EXCLUDED.faction, estimated_time_minutes = EXCLUDED.estimated_time_minutes, "
            "tags_json = EXCLUDED.tags_json, rewards_summary_json = EXCLUDED.rewards_summary_json, "
            "completion_rate = EXCLUDED.completion_rate, average_rating = EXCLUDED.average_rating, "
            "full_description = EXCLUDED.full_description, storyline = EXCLUDED.storyline, "
            "objectives_json = EXCLUDED.objectives_json, key_npcs_json = EXCLUDED.key_npcs_json, "
            "locations_json = EXCLUDED.locations_json, prerequisites_json = EXCLUDED.prerequisites_json, "
            "unlocks_json = EXCLUDED.unlocks_json, branches_count = EXCLUDED.branches_count, "
            "endings_count = EXCLUDED.endings_count, has_dialogue_tree = EXCLUDED.has_dialogue_tree, "
            "has_skill_checks = EXCLUDED.has_skill_checks, has_combat = EXCLUDED.has_combat, has_romance = EXCLUDED.has_romance, "
            "rewards_detailed_json = EXCLUDED.rewards_detailed_json, dialogue_tree_json = EXCLUDED.dialogue_tree_json, "
            "loot_table_json = EXCLUDED.loot_table_json, metadata_json = EXCLUDED.metadata_json, updated_at = CURRENT_TIMESTAMP;"
        )
        statements.append(insert_stmt)
    return "\n".join(statements) + "\n"


def main():
    entries = collect_entries()
    sql_content = compose_sql(entries)
    OUTPUT_FILE.parent.mkdir(parents=True, exist_ok=True)
    OUTPUT_FILE.write_text(sql_content, encoding="utf-8")
    print(f"Generated {len(entries)} quest entries -> {OUTPUT_FILE}")


if __name__ == "__main__":
    main()

