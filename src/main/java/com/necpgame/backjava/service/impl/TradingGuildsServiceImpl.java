package com.necpgame.backjava.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.necpgame.backjava.entity.TradingGuildEntity;
import com.necpgame.backjava.entity.TradingGuildMemberEntity;
import com.necpgame.backjava.entity.TradingGuildMemberId;
import com.necpgame.backjava.entity.TradingGuildTreasuryEntity;
import com.necpgame.backjava.entity.enums.TradingGuildRole;
import com.necpgame.backjava.entity.enums.TradingGuildType;
import com.necpgame.backjava.model.AddGuildMemberRequest;
import com.necpgame.backjava.model.CreateGuildRequest;
import com.necpgame.backjava.model.GetGuildMembers200Response;
import com.necpgame.backjava.model.GuildMember;
import com.necpgame.backjava.model.ListTradingGuilds200Response;
import com.necpgame.backjava.model.PaginationMeta;
import com.necpgame.backjava.model.TradingGuild;
import com.necpgame.backjava.model.TradingGuildDetailed;
import com.necpgame.backjava.model.TradingGuildDetailedAllOfBonuses;
import com.necpgame.backjava.model.TradingGuildDetailedAllOfGuildMaster;
import com.necpgame.backjava.model.TradingGuildDetailedAllOfUpgrades;
import com.necpgame.backjava.model.UpgradeGuildRequest;
import com.necpgame.backjava.repository.TradingGuildMemberRepository;
import com.necpgame.backjava.repository.TradingGuildRepository;
import com.necpgame.backjava.repository.TradingGuildTreasuryRepository;
import com.necpgame.backjava.repository.specification.TradingGuildSpecifications;
import com.necpgame.backjava.service.TradingGuildsService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TradingGuildsServiceImpl implements TradingGuildsService {

    private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<>() {
    };

    private final TradingGuildRepository tradingGuildRepository;
    private final TradingGuildMemberRepository tradingGuildMemberRepository;
    private final TradingGuildTreasuryRepository tradingGuildTreasuryRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public TradingGuild createTradingGuild(CreateGuildRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        }

        if (tradingGuildRepository.existsByNameIgnoreCase(request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Guild with the same name already exists");
        }

        TradingGuildType guildType = toGuildType(request.getType());
        UUID leaderId = request.getCharacterId();
        UUID guildId = UUID.randomUUID();

        TradingGuildEntity entity = TradingGuildEntity.builder()
            .id(guildId)
            .name(request.getName())
            .type(guildType)
            .level(1)
            .reputationScore(0)
            .memberCount(1)
            .founderId(leaderId)
            .leaderId(leaderId)
            .leaderName(null)
            .headquartersLocation(request.getHeadquartersLocation())
            .description(request.getDescription())
            .totalCapital(BigDecimal.ZERO)
            .specializationJson(writeStringList(defaultSpecializations(guildType)))
            .tradingFeeReduction(BigDecimal.ZERO)
            .profitMarginIncrease(BigDecimal.ZERO)
            .exclusiveRoutesCount(0)
            .guildHallLevel(1)
            .warehouseCapacity(0)
            .tradeOfficeCount(0)
            .build();

        TradingGuildEntity savedGuild;
        try {
            savedGuild = tradingGuildRepository.save(entity);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Unable to create guild", ex);
        }

        TradingGuildTreasuryEntity treasury = TradingGuildTreasuryEntity.builder()
            .guildId(guildId)
            .balance(BigDecimal.ZERO)
            .currenciesJson(writeDefaultCurrencyState())
            .assetsJson("[]")
            .build();
        tradingGuildTreasuryRepository.save(treasury);

        TradingGuildMemberEntity founder = TradingGuildMemberEntity.builder()
            .id(new TradingGuildMemberId(guildId, leaderId))
            .role(TradingGuildRole.GUILD_MASTER)
            .characterName(null)
            .contributionTotal(BigDecimal.ZERO)
            .votingPower(BigDecimal.ONE)
            .tradesCompleted(0)
            .joinedAt(Instant.now())
            .build();
        tradingGuildMemberRepository.save(founder);

        return mapToTradingGuild(savedGuild);
    }

    @Override
    @Transactional
    public Void addGuildMember(UUID guildId, AddGuildMemberRequest addGuildMemberRequest) {
        TradingGuildEntity guild = findGuildOrThrow(guildId);
        if (addGuildMemberRequest == null || addGuildMemberRequest.getCharacterId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "character_id is required");
        }

        UUID characterId = addGuildMemberRequest.getCharacterId();
        TradingGuildRole role = Optional.ofNullable(addGuildMemberRequest.getRole())
            .map(roleEnum -> TradingGuildRole.valueOf(roleEnum.getValue()))
            .orElse(TradingGuildRole.RECRUIT);

        TradingGuildMemberEntity member = tradingGuildMemberRepository
            .findByIdGuildIdAndIdCharacterId(guildId, characterId)
            .orElseGet(() -> TradingGuildMemberEntity.builder()
                .id(new TradingGuildMemberId(guildId, characterId))
                .contributionTotal(BigDecimal.ZERO)
                .votingPower(BigDecimal.ONE)
                .tradesCompleted(0)
                .joinedAt(Instant.now())
                .build());

        member.setRole(role);
        tradingGuildMemberRepository.save(member);

        guild.setMemberCount((int) tradingGuildMemberRepository.countByIdGuildId(guildId));
        tradingGuildRepository.save(guild);
        return null;
    }

    @Override
    public GetGuildMembers200Response getGuildMembers(UUID guildId) {
        findGuildOrThrow(guildId);
        List<TradingGuildMemberEntity> members = tradingGuildMemberRepository.findByIdGuildId(guildId);

        List<GuildMember> memberModels = new ArrayList<>();
        Map<String, Integer> roleDistribution = new HashMap<>();

        for (TradingGuildMemberEntity member : members) {
            GuildMember model = new GuildMember()
                .characterId(member.getId().getCharacterId())
                .characterName(member.getCharacterName())
                .role(member.getRole() != null ? GuildMember.RoleEnum.fromValue(member.getRole().name()) : null)
                .contributionTotal(toInteger(member.getContributionTotal()))
                .tradesCompleted(Optional.ofNullable(member.getTradesCompleted()).orElse(0))
                .joinedAt(toOffsetDateTime(member.getJoinedAt()));
            memberModels.add(model);

            String key = member.getRole() != null ? member.getRole().name() : TradingGuildRole.RECRUIT.name();
            roleDistribution.merge(key, 1, Integer::sum);
        }

        return new GetGuildMembers200Response()
            .members(memberModels)
            .rolesDistribution(roleDistribution);
    }

    @Override
    public TradingGuildDetailed getTradingGuild(UUID guildId) {
        TradingGuildEntity entity = findGuildOrThrow(guildId);
        return mapToTradingGuildDetailed(entity);
    }

    @Override
    public ListTradingGuilds200Response listTradingGuilds(String type, Integer minLevel, String region,
                                                          Integer page, Integer pageSize) {
        int safePage = page != null && page > 0 ? page : 1;
        int safePageSize = pageSize != null && pageSize > 0 ? pageSize : 20;

        Specification<TradingGuildEntity> specification = Specification
            .where(TradingGuildSpecifications.hasType(type))
            .and(TradingGuildSpecifications.hasMinLevel(minLevel))
            .and(TradingGuildSpecifications.locatedInRegion(region));

        Pageable pageable = PageRequest.of(safePage - 1, safePageSize);
        Page<TradingGuildEntity> resultPage = tradingGuildRepository.findAll(specification, pageable);

        List<TradingGuild> guilds = resultPage.stream()
            .map(this::mapToTradingGuild)
            .toList();

        PaginationMeta meta = new PaginationMeta()
            .page(safePage)
            .pageSize(safePageSize)
            .total(Math.toIntExact(resultPage.getTotalElements()))
            .totalPages(resultPage.getTotalPages())
            .hasNext(resultPage.hasNext())
            .hasPrev(resultPage.hasPrevious());

        return new ListTradingGuilds200Response()
            .data(guilds)
            .meta(meta);
    }

    @Override
    @Transactional
    public Void upgradeGuild(UUID guildId, UpgradeGuildRequest upgradeGuildRequest) {
        TradingGuildEntity guild = findGuildOrThrow(guildId);
        UpgradeGuildRequest.UpgradeTypeEnum upgradeType = upgradeGuildRequest != null ? upgradeGuildRequest.getUpgradeType() : null;
        if (upgradeType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "upgrade_type is required");
        }

        switch (upgradeType) {
            case LEVEL_UP -> guild.setLevel(Math.min(guild.getLevel() + 1, 5));
            case GUILD_HALL -> guild.setGuildHallLevel(Optional.ofNullable(guild.getGuildHallLevel()).orElse(1) + 1);
            case WAREHOUSE -> guild.setWarehouseCapacity(Optional.ofNullable(guild.getWarehouseCapacity()).orElse(0) + 250);
            case TRADE_OFFICE -> guild.setTradeOfficeCount(Optional.ofNullable(guild.getTradeOfficeCount()).orElse(0) + 1);
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported upgrade type");
        }

        tradingGuildRepository.save(guild);
        return null;
    }

    private TradingGuildEntity findGuildOrThrow(UUID guildId) {
        return tradingGuildRepository.findById(guildId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guild not found"));
    }

    private TradingGuild mapToTradingGuild(TradingGuildEntity entity) {
        TradingGuild model = new TradingGuild()
            .guildId(entity.getId())
            .name(entity.getName())
            .type(entity.getType() != null ? TradingGuild.TypeEnum.fromValue(entity.getType().name()) : null)
            .level(entity.getLevel())
            .memberCount(entity.getMemberCount())
            .headquartersLocation(entity.getHeadquartersLocation())
            .reputation(entity.getReputationScore())
            .createdAt(toOffsetDateTime(entity.getCreatedAt()));
        return model;
    }

    private TradingGuildDetailed mapToTradingGuildDetailed(TradingGuildEntity entity) {
        TradingGuildDetailedAllOfGuildMaster guildMaster = new TradingGuildDetailedAllOfGuildMaster()
            .characterId(entity.getLeaderId())
            .characterName(entity.getLeaderName());

        TradingGuildDetailedAllOfBonuses bonuses = new TradingGuildDetailedAllOfBonuses()
            .tradingFeeReduction(toFloat(entity.getTradingFeeReduction()))
            .profitMarginIncrease(entity.getProfitMarginIncrease())
            .exclusiveRoutesCount(entity.getExclusiveRoutesCount());

        TradingGuildDetailedAllOfUpgrades upgrades = new TradingGuildDetailedAllOfUpgrades()
            .guildHallLevel(entity.getGuildHallLevel())
            .warehouseCapacity(entity.getWarehouseCapacity())
            .tradeOfficeCount(entity.getTradeOfficeCount());

        TradingGuildDetailed detailed = new TradingGuildDetailed()
            .guildId(entity.getId())
            .name(entity.getName())
            .type(entity.getType() != null ? TradingGuildDetailed.TypeEnum.fromValue(entity.getType().name()) : null)
            .level(entity.getLevel())
            .memberCount(entity.getMemberCount())
            .headquartersLocation(entity.getHeadquartersLocation())
            .reputation(entity.getReputationScore())
            .createdAt(toOffsetDateTime(entity.getCreatedAt()))
            .guildMaster(guildMaster)
            .description(entity.getDescription())
            .specialization(readSpecializations(entity.getSpecializationJson()))
            .bonuses(bonuses)
            .upgrades(upgrades);

        return detailed;
    }

    private List<String> readSpecializations(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, STRING_LIST_TYPE);
        } catch (JsonProcessingException ex) {
            return Collections.emptyList();
        }
    }

    private String writeStringList(List<String> values) {
        try {
            return objectMapper.writeValueAsString(values);
        } catch (JsonProcessingException ex) {
            return "[]";
        }
    }

    private String writeDefaultCurrencyState() {
        Map<String, Integer> defaultCurrency = new HashMap<>();
        defaultCurrency.put("eddies", 0);
        try {
            return objectMapper.writeValueAsString(defaultCurrency);
        } catch (JsonProcessingException ex) {
            return "{\"eddies\":0}";
        }
    }

    private List<String> defaultSpecializations(TradingGuildType type) {
        return Collections.singletonList(type.name());
    }

    private OffsetDateTime toOffsetDateTime(Instant instant) {
        return instant != null ? OffsetDateTime.ofInstant(instant, ZoneOffset.UTC) : null;
    }

    private int toInteger(BigDecimal value) {
        if (value == null) {
            return 0;
        }
        return value.intValue();
    }

    private Float toFloat(BigDecimal value) {
        return value != null ? value.floatValue() : null;
    }

    private TradingGuildType toGuildType(CreateGuildRequest.TypeEnum typeEnum) {
        if (typeEnum == null) {
            return TradingGuildType.MIXED;
        }
        return TradingGuildType.valueOf(typeEnum.getValue());
    }
}

