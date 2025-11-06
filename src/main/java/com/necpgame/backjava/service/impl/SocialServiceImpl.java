package com.necpgame.backjava.service.impl;

import com.necpgame.backjava.model.*;
import com.necpgame.backjava.repository.FriendshipRepository;
import com.necpgame.backjava.repository.PartyRepository;
import com.necpgame.backjava.service.SocialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

/**
 * Реализация сервиса социальных механик (Party System)
 * Соответствует party-system.yaml
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SocialServiceImpl implements SocialService {

    private final PartyRepository partyRepository;
    private final FriendshipRepository friendshipRepository;

    @Override
    public Party createParty(CreatePartyRequest request) {
        log.info("Creating party with leader: {}", request.getLeaderCharacterId());

        return new Party()
            .partyId("party_" + System.currentTimeMillis())
            .leaderId(request.getLeaderCharacterId())
            .maxMembers(request.getMaxMembers());
    }

    @Override
    public PartyDetails getParty(String partyId) {
        log.info("Getting party: {}", partyId);

        return new PartyDetails();
    }

    @Override
    public Object inviteToParty(String partyId, InviteToPartyRequest request) {
        log.info("Inviting to party {}", partyId);

        return Map.of("status", "pending");
    }

    @Override
    public Party joinParty(String partyId, JoinPartyRequest request) {
        String characterId = request != null && request.getCharacterId() != null ? request.getCharacterId() : "unknown";
        log.info("Character {} joining party {}", characterId, partyId);

        return new Party();
    }

    @Override
    public Object leaveParty(String partyId, JoinPartyRequest request) {
        String characterId = request != null && request.getCharacterId() != null ? request.getCharacterId() : "unknown";
        log.info("Character {} leaving party {}", characterId, partyId);

        return Map.of("status", "pending");
    }

    @Override
    public Object acceptFriendRequest(String requestId) {
        log.info("Accepting friend request {}", requestId);

        return Map.of("status", "pending");
    }

    @Override
    public Object blockPlayer(BlockPlayerRequest request) {
        String blockerId = request != null ? request.getPlayerId() : "unknown";
        String blockedId = request != null ? request.getTargetPlayerId() : "unknown";
        log.info("Player {} blocking {}", blockerId, blockedId);

        return Map.of("status", "pending");
    }

    @Override
    public GetFriends200Response getFriends(String playerId) {
        log.info("Fetching friends for player {}", playerId);

        return new GetFriends200Response().friends(Collections.emptyList());
    }

    @Override
    public Object removeFriend(String friendId) {
        log.info("Removing friend {}", friendId);

        return Map.of("status", "pending");
    }

    @Override
    public Object sendFriendRequest(SendFriendRequestRequest request) {
        String requester = request != null ? request.getPlayerId() : "unknown";
        String target = request != null ? request.getTargetPlayerName() : "unknown";
        log.info("Player {} sending friend request to {}", requester, target);

        return Map.of("status", "pending");
    }
}

