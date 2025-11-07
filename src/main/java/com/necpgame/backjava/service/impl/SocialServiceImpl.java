package com.necpgame.backjava.service.impl;

import com.necpgame.backjava.entity.GuildEntity;
import com.necpgame.backjava.entity.GuildMemberEntity;
import com.necpgame.backjava.entity.GuildMemberId;
import com.necpgame.backjava.model.*;
import com.necpgame.backjava.repository.FriendshipRepository;
import com.necpgame.backjava.repository.GuildMemberRepository;
import com.necpgame.backjava.repository.GuildRepository;
import com.necpgame.backjava.repository.PartyRepository;
import com.necpgame.backjava.service.SocialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
    private final GuildRepository guildRepository;
    private final GuildMemberRepository guildMemberRepository;

    @Override
    public Guild createGuild(CreateGuildRequest request) {
        String tag = generateTag(request.getName());
        log.info("Creating guild {} with generated tag {}", request.getName(), tag);
        GuildEntity entity = GuildEntity.builder()
            .name(request.getName())
            .tag(tag)
            .level(1)
            .build();
        GuildEntity savedGuild = guildRepository.save(entity);
        UUID founderId = request.getCharacterId();
        if (founderId != null) {
            GuildMemberEntity member = GuildMemberEntity.builder()
                .id(GuildMemberId.builder()
                    .guildId(savedGuild.getId())
                    .characterId(founderId)
                    .build())
                .rank("LEADER")
                .joinedAt(OffsetDateTime.now())
                .build();
            guildMemberRepository.save(member);
        }
        long members = guildMemberRepository.countByIdGuildId(savedGuild.getId());
        return toGuild(savedGuild, members);
    }

    @Override
    public GuildDetails getGuild(String guildId) {
        log.info("Fetching guild {}", guildId);
        UUID id = parseUuidSafe(guildId);
        if (id == null) {
            return new GuildDetails();
        }
        Optional<GuildEntity> guildOpt = guildRepository.findById(id);
        if (guildOpt.isEmpty()) {
            return new GuildDetails();
        }
        List<GuildMemberEntity> members = guildMemberRepository.findByIdGuildId(id);
        return toGuildDetails(guildOpt.get(), members);
    }

    @Override
    public Object inviteToGuild(String guildId, InviteToGuildRequest inviteToGuildRequest) {
        log.info("Inviting {} to guild {}", inviteToGuildRequest != null ? inviteToGuildRequest.getInviteeCharacterName() : "unknown", guildId);
        return Map.of("status", "pending");
    }

    @Override
    public Object joinGuild(String guildId, JoinGuildRequest joinGuildRequest) {
        UUID gId = parseUuidSafe(guildId);
        UUID characterId = joinGuildRequest != null ? parseUuidSafe(joinGuildRequest.getCharacterId()) : null;
        if (gId != null && characterId != null) {
            GuildMemberEntity member = GuildMemberEntity.builder()
                .id(GuildMemberId.builder()
                    .guildId(gId)
                    .characterId(characterId)
                    .build())
                .rank("MEMBER")
                .joinedAt(OffsetDateTime.now())
                .build();
            guildMemberRepository.save(member);
        }
        return Map.of("status", "pending");
    }

    @Override
    public Object leaveGuild(String guildId, JoinGuildRequest joinGuildRequest) {
        UUID gId = parseUuidSafe(guildId);
        UUID characterId = joinGuildRequest != null ? parseUuidSafe(joinGuildRequest.getCharacterId()) : null;
        if (gId != null && characterId != null) {
            GuildMemberId id = GuildMemberId.builder()
                .guildId(gId)
                .characterId(characterId)
                .build();
            guildMemberRepository.deleteById(id);
        }
        return Map.of("status", "pending");
    }

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

    private UUID parseUuidSafe(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            log.warn("Invalid UUID provided: {}", value);
            return null;
        }
    }

    private String generateTag(String name) {
        if (name == null || name.isBlank()) {
            return "GUILD";
        }
        String simplified = name.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        if (simplified.isEmpty()) {
            simplified = "GUILD";
        }
        return simplified.length() > 4 ? simplified.substring(0, 4) : simplified;
    }

    private Guild toGuild(GuildEntity entity, long memberCount) {
        return new Guild()
            .guildId(entity.getId() != null ? entity.getId().toString() : null)
            .name(entity.getName())
            .tag(entity.getTag())
            .level(entity.getLevel())
            .memberCount((int) Math.min(memberCount, Integer.MAX_VALUE))
            .createdAt(entity.getCreatedAt());
    }

    private GuildDetails toGuildDetails(GuildEntity entity, List<GuildMemberEntity> members) {
        long memberCount = guildMemberRepository.countByIdGuildId(entity.getId());
        GuildDetails details = new GuildDetails()
            .guildId(entity.getId() != null ? entity.getId().toString() : null)
            .name(entity.getName())
            .tag(entity.getTag())
            .level(entity.getLevel())
            .memberCount((int) Math.min(memberCount, Integer.MAX_VALUE))
            .createdAt(entity.getCreatedAt());

        List<Object> memberDtos = new ArrayList<>();
        for (GuildMemberEntity member : members) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("character_id", member.getId().getCharacterId().toString());
            payload.put("rank", member.getRank());
            payload.put("joined_at", member.getJoinedAt());
            memberDtos.add(payload);
        }
        details.setMembers(memberDtos);
        return details;
    }
}

