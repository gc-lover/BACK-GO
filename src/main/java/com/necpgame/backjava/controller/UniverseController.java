package com.necpgame.backjava.controller;

import com.necpgame.backjava.api.UniverseApi;
import com.necpgame.backjava.model.GetCharacterCodex200Response;
import com.necpgame.backjava.model.GetTimeline200Response;
import com.necpgame.backjava.model.SearchLore200Response;
import com.necpgame.backjava.model.UniverseLore;
import com.necpgame.backjava.model.UnlockCodexEntryRequest;
import com.necpgame.backjava.service.UniverseService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UniverseController implements UniverseApi {

    private final UniverseService universeService;

    @Override
    public ResponseEntity<GetCharacterCodex200Response> getCharacterCodex(UUID characterId) {
        return ResponseEntity.ok(universeService.getCharacterCodex(characterId));
    }

    @Override
    public ResponseEntity<GetTimeline200Response> getTimeline(String era, String eventType) {
        return ResponseEntity.ok(universeService.getTimeline(era, eventType));
    }

    @Override
    public ResponseEntity<UniverseLore> getUniverseLore() {
        return ResponseEntity.ok(universeService.getUniverseLore());
    }

    @Override
    public ResponseEntity<SearchLore200Response> searchLore(String q, String category) {
        return ResponseEntity.ok(universeService.searchLore(q, category));
    }

    @Override
    public ResponseEntity<Void> unlockCodexEntry(UnlockCodexEntryRequest unlockCodexEntryRequest) {
        universeService.unlockCodexEntry(unlockCodexEntryRequest);
        return ResponseEntity.ok().build();
    }
}

