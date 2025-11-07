package com.necpgame.backjava.service;

import com.necpgame.backjava.model.GetCharacterCodex200Response;
import com.necpgame.backjava.model.GetTimeline200Response;
import com.necpgame.backjava.model.SearchLore200Response;
import com.necpgame.backjava.model.UniverseLore;
import com.necpgame.backjava.model.UnlockCodexEntryRequest;
import java.util.UUID;

public interface UniverseService {

    GetCharacterCodex200Response getCharacterCodex(UUID characterId);

    GetTimeline200Response getTimeline(String era, String eventType);

    UniverseLore getUniverseLore();

    SearchLore200Response searchLore(String q, String category);

    Void unlockCodexEntry(UnlockCodexEntryRequest request);
}

