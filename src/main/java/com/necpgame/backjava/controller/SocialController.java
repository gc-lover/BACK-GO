package com.necpgame.backjava.controller;

import com.necpgame.backjava.api.SocialApi;
import com.necpgame.backjava.model.*;
import com.necpgame.backjava.service.SocialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SocialController implements SocialApi {

    private final SocialService socialService;

    @Override
    public ResponseEntity<Guild> createGuild(CreateGuildRequest createGuildRequest) {
        return ResponseEntity.ok(socialService.createGuild(createGuildRequest));
    }

    @Override
    public ResponseEntity<GuildDetails> getGuild(String guildId) {
        return ResponseEntity.ok(socialService.getGuild(guildId));
    }

    @Override
    public ResponseEntity<Object> inviteToGuild(String guildId, InviteToGuildRequest inviteToGuildRequest) {
        return ResponseEntity.ok(socialService.inviteToGuild(guildId, inviteToGuildRequest));
    }

    @Override
    public ResponseEntity<Object> joinGuild(String guildId, JoinGuildRequest joinGuildRequest) {
        return ResponseEntity.ok(socialService.joinGuild(guildId, joinGuildRequest));
    }

    @Override
    public ResponseEntity<Object> leaveGuild(String guildId, JoinGuildRequest joinGuildRequest) {
        return ResponseEntity.ok(socialService.leaveGuild(guildId, joinGuildRequest));
    }

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

    @Override
    public ResponseEntity<Object> acceptFriendRequest(String requestId) {
        return ResponseEntity.ok(socialService.acceptFriendRequest(requestId));
    }

    @Override
    public ResponseEntity<Object> blockPlayer(BlockPlayerRequest blockPlayerRequest) {
        return ResponseEntity.ok(socialService.blockPlayer(blockPlayerRequest));
    }

    @Override
    public ResponseEntity<GetFriends200Response> getFriends(String playerId) {
        return ResponseEntity.ok(socialService.getFriends(playerId));
    }

    @Override
    public ResponseEntity<Object> removeFriend(String friendId) {
        return ResponseEntity.ok(socialService.removeFriend(friendId));
    }

    @Override
    public ResponseEntity<Object> sendFriendRequest(SendFriendRequestRequest sendFriendRequestRequest) {
        return ResponseEntity.ok(socialService.sendFriendRequest(sendFriendRequestRequest));
    }
}

