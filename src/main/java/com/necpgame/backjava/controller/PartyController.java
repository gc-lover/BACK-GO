package com.necpgame.backjava.controller;

import com.necpgame.backjava.api.SocialApi;
import com.necpgame.backjava.model.*;
import com.necpgame.backjava.service.SocialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller для системы групп (Party)
 * Implements SocialApi - сгенерирован из party-system.yaml
 */
@RestController
@RequiredArgsConstructor
public class PartyController implements SocialApi {

    private final SocialService socialService;

    @Override
    public ResponseEntity<Party> createParty(CreatePartyRequest createPartyRequest) {
        return ResponseEntity.ok(socialService.createParty(createPartyRequest));
    }

    @Override
    public ResponseEntity<PartyDetails> getParty(String partyId) {
        return ResponseEntity.ok(socialService.getParty(partyId));
    }

    @Override
    public ResponseEntity<Object> inviteToParty(String partyId, InviteToPartyRequest inviteToPartyRequest) {
        return ResponseEntity.ok(socialService.inviteToParty(partyId, inviteToPartyRequest));
    }

    @Override
    public ResponseEntity<Party> joinParty(String partyId, JoinPartyRequest joinPartyRequest) {
        return ResponseEntity.ok(socialService.joinParty(partyId, joinPartyRequest));
    }

    @Override
    public ResponseEntity<Object> leaveParty(String partyId, JoinPartyRequest joinPartyRequest) {
        return ResponseEntity.ok(socialService.leaveParty(partyId, joinPartyRequest));
    }
}

