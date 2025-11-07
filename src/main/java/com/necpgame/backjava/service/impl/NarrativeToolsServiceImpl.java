package com.necpgame.backjava.service.impl;

import com.necpgame.backjava.model.GenerateNPC200Response;
import com.necpgame.backjava.model.GenerateNPCRequest;
import com.necpgame.backjava.model.GenerateQuestRequest;
import com.necpgame.backjava.model.ValidateNarrative200Response;
import com.necpgame.backjava.model.ValidateNarrativeRequest;
import com.necpgame.backjava.service.NarrativeToolsService;
import org.springframework.stereotype.Service;

@Service
public class NarrativeToolsServiceImpl implements NarrativeToolsService {

    private UnsupportedOperationException error() {
        return new UnsupportedOperationException("Narrative tools service is not implemented yet");
    }

    @Override
    public GenerateNPC200Response generateNPC(GenerateNPCRequest generateNPCRequest) {
        throw error();
    }

    @Override
    public Object generateQuest(GenerateQuestRequest generateQuestRequest) {
        throw error();
    }

    @Override
    public ValidateNarrative200Response validateNarrative(ValidateNarrativeRequest validateNarrativeRequest) {
        throw error();
    }
}

