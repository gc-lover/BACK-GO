package com.necpgame.backjava.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.necpgame.backjava.entity.AchievementEntity;
import com.necpgame.backjava.entity.FriendshipEntity;
import com.necpgame.backjava.entity.GuildLeaderboardEntryEntity;
import com.necpgame.backjava.entity.PlayerAchievementEntity;
import com.necpgame.backjava.entity.PlayerAchievementId;
import com.necpgame.backjava.entity.PlayerTitleEntity;
import com.necpgame.backjava.entity.LeaderboardEntryEntity;
import com.necpgame.backjava.entity.LeaderboardEntryId;
import com.necpgame.backjava.entity.LeaderboardSeasonEntity;
import com.necpgame.backjava.entity.SeasonalLeaderboardEntryEntity;
import com.necpgame.backjava.entity.SeasonalLeaderboardEntryId;
import com.necpgame.backjava.model.AchievementDefinition;
import com.necpgame.backjava.model.AchievementDefinition.CategoryEnum;
import com.necpgame.backjava.model.AchievementDefinition.RarityEnum;
import com.necpgame.backjava.model.AchievementDefinitionRequirements;
import com.necpgame.backjava.model.AchievementRewards;
import com.necpgame.backjava.model.GetPlayerAchievements200Response;
import com.necpgame.backjava.model.GetPlayerTitles200Response;
import com.necpgame.backjava.model.ListAchievements200Response;
import com.necpgame.backjava.model.GetSeasonalLeaderboard200Response;
import com.necpgame.backjava.model.GuildLeaderboardEntry;
import com.necpgame.backjava.model.GuildLeaderboardResponse;
import com.necpgame.backjava.model.GuildRankResponse;
import com.necpgame.backjava.model.LeaderboardEntry;
import com.necpgame.backjava.model.LeaderboardEntryGuild;
import com.necpgame.backjava.model.LeaderboardResponse;
import com.necpgame.backjava.model.PaginationMeta;
import com.necpgame.backjava.model.PlayerAchievement;
import com.necpgame.backjava.model.PlayerAchievement.StatusEnum;
import com.necpgame.backjava.model.PlayerAchievementProgress;
import com.necpgame.backjava.model.PlayerRankResponse;
import com.necpgame.backjava.model.ProgressUpdateResult;
import com.necpgame.backjava.model.Season;
import com.necpgame.backjava.model.SetActiveTitleRequest;
import com.necpgame.backjava.model.Title;
import com.necpgame.backjava.model.UpdateLeaderboardScore200Response;
import com.necpgame.backjava.model.UpdateProgressRequest;
import com.necpgame.backjava.model.UpdateScoreRequest;
import com.necpgame.backjava.repository.AchievementRepository;
import com.necpgame.backjava.repository.FriendshipRepository;
import com.necpgame.backjava.repository.GuildLeaderboardEntryRepository;
import com.necpgame.backjava.repository.GuildMemberRepository;
import com.necpgame.backjava.repository.GuildRepository;
import com.necpgame.backjava.repository.LeaderboardEntryRepository;
import com.necpgame.backjava.repository.LeaderboardSeasonRepository;
import com.necpgame.backjava.repository.PlayerAchievementRepository;
import com.necpgame.backjava.repository.PlayerTitleRepository;
import com.necpgame.backjava.repository.SeasonalLeaderboardEntryRepository;
import com.necpgame.backjava.service.ProgressionService;
import com.necpgame.backjava.util.OffsetBasedPageRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProgressionServiceImpl implements ProgressionService {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int DEFAULT_LEADERBOARD_LIMIT = 100;
    private static final int DEFAULT_NEARBY_WINDOW = 5;

    private final AchievementRepository achievementRepository;
    private final PlayerAchievementRepository playerAchievementRepository;
    private final PlayerTitleRepository playerTitleRepository;
    private final LeaderboardEntryRepository leaderboardEntryRepository;
    private final SeasonalLeaderboardEntryRepository seasonalLeaderboardEntryRepository;
    private final LeaderboardSeasonRepository leaderboardSeasonRepository;
    private final GuildLeaderboardEntryRepository guildLeaderboardEntryRepository;
    private final FriendshipRepository friendshipRepository;
    private final GuildRepository guildRepository;
    private final GuildMemberRepository guildMemberRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public ListAchievements200Response listAchievements(String category, String rarity, Boolean unlocked, Integer page, Integer pageSize) {
        Pageable pageable = buildPageable(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Specification<AchievementEntity> specification = buildAchievementSpecification(category, rarity, unlocked);
        Page<AchievementEntity> achievementPage = achievementRepository.findAll(specification, pageable);
        List<AchievementDefinition> data = achievementPage.stream()
            .map(this::toAchievementDefinition)
            .collect(Collectors.toList());
        return new ListAchievements200Response()
            .data(data)
            .meta(toPaginationMeta(achievementPage));
    }

    @Override
    @Transactional(readOnly = true)
    public AchievementDefinition getAchievement(UUID achievementId) {
        return achievementRepository.findById(achievementId)
            .map(this::toAchievementDefinition)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Achievement not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public GetPlayerAchievements200Response getPlayerAchievements(UUID playerId, String status, Integer page, Integer pageSize) {
        Pageable pageable = buildPageable(page, pageSize, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<PlayerAchievementEntity> progressPage = fetchPlayerAchievements(playerId, status, pageable);
        List<PlayerAchievement> achievements = progressPage.stream()
            .map(this::toPlayerAchievement)
            .collect(Collectors.toList());
        return new GetPlayerAchievements200Response()
            .data(achievements)
            .meta(toPaginationMeta(progressPage));
    }

    @Override
    @Transactional(readOnly = true)
    public GetPlayerTitles200Response getPlayerTitles(UUID playerId) {
        List<PlayerTitleEntity> titleEntities = playerTitleRepository.findByIdPlayerId(playerId);
        List<Title> titles = titleEntities.stream()
            .map(this::toTitle)
            .collect(Collectors.toList());
        String activeTitle = playerTitleRepository.findByIdPlayerIdAndActiveTrue(playerId)
            .map(entity -> entity.getId().getTitleId().toString())
            .orElse(null);
        return new GetPlayerTitles200Response()
            .titles(titles)
            .activeTitle(activeTitle);
    }

    @Override
    public Void setActiveTitle(UUID playerId, SetActiveTitleRequest setActiveTitleRequest) {
        if (setActiveTitleRequest == null || setActiveTitleRequest.getTitleId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title_id is required");
        }
        UUID targetId = setActiveTitleRequest.getTitleId();
        playerTitleRepository.findByIdPlayerIdAndIdTitleId(playerId, targetId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Title not found"));
        List<PlayerTitleEntity> titles = playerTitleRepository.findByIdPlayerId(playerId);
        for (PlayerTitleEntity entity : titles) {
            boolean isTarget = entity.getId().getTitleId().equals(targetId);
            entity.setActive(isTarget);
        }
        playerTitleRepository.saveAll(titles);
        return null;
    }

    @Override
    public ProgressUpdateResult updateAchievementProgress(UUID playerId, UpdateProgressRequest updateProgressRequest) {
        if (updateProgressRequest == null || updateProgressRequest.getAchievementId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "achievement_id is required");
        }
        UUID achievementId = updateProgressRequest.getAchievementId();
        AchievementEntity achievement = achievementRepository.findById(achievementId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Achievement not found"));
        PlayerAchievementEntity entity = playerAchievementRepository
            .findByIdPlayerIdAndIdAchievementId(playerId, achievementId)
            .orElseGet(() -> initializePlayerAchievement(playerId, achievement));
        if (entity.getAchievement() == null) {
            entity.setAchievement(achievement);
        }
        int increment = updateProgressRequest.getIncrement() != null ? updateProgressRequest.getIncrement() : 1;
        if (increment < 0) {
            increment = 0;
        }
        int previousProgress = entity.getCurrentProgress();
        int currentProgress = previousProgress + increment;
        entity.setCurrentProgress(currentProgress);
        entity.setTargetProgress(resolveTargetProgress(entity, achievement));
        entity.setProgressPercent(calculatePercentage(entity));
        boolean unlocked = isUnlocked(entity);
        if (unlocked) {
            entity.setStatus("UNLOCKED");
            if (entity.getUnlockedAt() == null) {
                entity.setUnlockedAt(OffsetDateTime.now());
            }
        } else if (currentProgress > 0) {
            entity.setStatus("IN_PROGRESS");
        } else {
            entity.setStatus("LOCKED");
        }
        PlayerAchievementEntity saved = playerAchievementRepository.save(entity);
        ProgressUpdateResult result = new ProgressUpdateResult()
            .achievementId(achievementId)
            .previousProgress(previousProgress)
            .newProgress(saved.getCurrentProgress())
            .unlocked(unlocked);
        if (unlocked) {
            result.rewardsGranted(readRewards(achievement.getRewards()));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public LeaderboardResponse getFriendLeaderboard(UUID playerId, String category) {
        Set<UUID> relatedPlayers = new HashSet<>();
        relatedPlayers.add(playerId);
        relatedPlayers.addAll(resolveFriendIds(playerId));

        List<LeaderboardEntryEntity> entries = leaderboardEntryRepository.findByIdCategoryAndIdPlayerIdIn(
            category,
            relatedPlayers,
            Sort.by(Sort.Direction.DESC, "score").and(Sort.by(Sort.Direction.ASC, "updatedAt"))
        );

        List<LeaderboardEntry> mapped = mapLeaderboardEntries(entries, 0L);
        OffsetDateTime updatedAt = resolveLatestUpdatedAt(entries);

        return new LeaderboardResponse()
            .category(category)
            .leaderboardType(LeaderboardResponse.LeaderboardTypeEnum.FRIENDS)
            .entries(mapped)
            .totalEntries(mapped.size())
            .updatedAt(updatedAt);
    }

    @Override
    @Transactional(readOnly = true)
    public LeaderboardResponse getGlobalLeaderboard(String category, Integer top, Integer offset) {
        int limit = resolveLimit(top);
        long skip = resolveOffset(offset);
        Pageable pageable = new OffsetBasedPageRequest(skip, limit, Sort.by(Sort.Direction.DESC, "score").and(Sort.by(Sort.Direction.ASC, "updatedAt")));
        Page<LeaderboardEntryEntity> page = leaderboardEntryRepository.findByIdCategory(category, pageable);

        List<LeaderboardEntry> mapped = mapLeaderboardEntries(page.getContent(), pageable.getOffset());
        OffsetDateTime updatedAt = resolveLatestUpdatedAt(page.getContent());
        long total = leaderboardEntryRepository.countByIdCategory(category);

        return new LeaderboardResponse()
            .category(category)
            .leaderboardType(LeaderboardResponse.LeaderboardTypeEnum.GLOBAL)
            .entries(mapped)
            .totalEntries(safeLongToInt(total))
            .updatedAt(updatedAt);
    }

    @Override
    @Transactional(readOnly = true)
    public GuildLeaderboardResponse getGuildLeaderboard(String category, Integer top) {
        int limit = resolveLimit(top);
        Pageable pageable = new OffsetBasedPageRequest(0, limit, Sort.by(Sort.Direction.DESC, "score").and(Sort.by(Sort.Direction.ASC, "updatedAt")));
        Page<GuildLeaderboardEntryEntity> page = guildLeaderboardEntryRepository.findByIdCategory(category, pageable);

        List<GuildLeaderboardEntry> mapped = mapGuildLeaderboardEntries(page.getContent(), pageable.getOffset());
        OffsetDateTime updatedAt = resolveLatestUpdatedAt(page.getContent());
        long total = guildLeaderboardEntryRepository.countByIdCategory(category);

        return new GuildLeaderboardResponse()
            .category(category)
            .entries(mapped)
            .totalGuilds(safeLongToInt(total))
            .updatedAt(updatedAt);
    }

    @Override
    @Transactional(readOnly = true)
    public GuildRankResponse getGuildRank(String category, UUID guildId) {
        GuildLeaderboardEntryEntity entity = guildLeaderboardEntryRepository.findByIdCategoryAndIdGuildId(category, guildId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guild not found in leaderboard"));

        int rank = safeLongToInt(guildLeaderboardEntryRepository.countByIdCategoryAndScoreGreaterThan(category, entity.getScore()) + 1);
        long total = guildLeaderboardEntryRepository.countByIdCategory(category);
        List<GuildLeaderboardEntry> nearby = resolveNearbyGuildEntries(category, rank, total);

        return new GuildRankResponse()
            .guildId(guildId)
            .category(category)
            .rank(rank)
            .score(entity.getScore())
            .totalGuilds(safeLongToInt(total))
            .nearbyGuilds(nearby);
    }

    @Override
    @Transactional(readOnly = true)
    public PlayerRankResponse getPlayerGlobalRank(String category, UUID playerId) {
        LeaderboardEntryEntity entity = leaderboardEntryRepository.findByIdCategoryAndIdPlayerId(category, playerId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found in leaderboard"));

        int rank = safeLongToInt(leaderboardEntryRepository.countByIdCategoryAndScoreGreaterThan(category, entity.getScore()) + 1);
        long total = leaderboardEntryRepository.countByIdCategory(category);
        Float percentile = calculatePercentile(rank, total);
        List<LeaderboardEntry> nearby = resolveNearbyPlayerEntries(category, rank, total);

        return new PlayerRankResponse()
            .playerId(playerId)
            .category(category)
            .rank(rank)
            .score(entity.getScore())
            .totalPlayers(safeLongToInt(total))
            .percentile(percentile)
            .nearbyEntries(nearby);
    }

    @Override
    @Transactional(readOnly = true)
    public GetSeasonalLeaderboard200Response getSeasonalLeaderboard(String seasonId, String category, Integer top) {
        int limit = resolveLimit(top);
        Pageable pageable = new OffsetBasedPageRequest(0, limit, Sort.by(Sort.Direction.DESC, "score").and(Sort.by(Sort.Direction.ASC, "updatedAt")));
        Page<SeasonalLeaderboardEntryEntity> page = seasonalLeaderboardEntryRepository.findByIdSeasonIdAndIdCategory(seasonId, category, pageable);

        List<LeaderboardEntry> mapped = mapSeasonalEntries(page.getContent(), pageable.getOffset());
        OffsetDateTime updatedAt = resolveLatestUpdatedAt(page.getContent());
        long total = seasonalLeaderboardEntryRepository.countByIdSeasonIdAndIdCategory(seasonId, category);

        LeaderboardSeasonEntity seasonEntity = leaderboardSeasonRepository.findById(seasonId).orElse(null);
        Season season = seasonEntity != null ? toSeason(seasonEntity) : new Season().seasonId(seasonId);

        return new GetSeasonalLeaderboard200Response()
            .category(category)
            .leaderboardType(GetSeasonalLeaderboard200Response.LeaderboardTypeEnum.SEASONAL)
            .entries(mapped)
            .totalEntries(safeLongToInt(total))
            .updatedAt(updatedAt)
            .season(season);
    }

    @Override
    public UpdateLeaderboardScore200Response updateLeaderboardScore(UpdateScoreRequest updateScoreRequest) {
        if (updateScoreRequest == null || updateScoreRequest.getPlayerId() == null || updateScoreRequest.getScore() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "player_id and score are required");
        }
        String category = updateScoreRequest.getCategory() != null ? updateScoreRequest.getCategory().getValue() : null;
        if (!StringUtils.hasText(category)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "category is required");
        }
        UUID playerId = updateScoreRequest.getPlayerId();
        BigDecimal score = updateScoreRequest.getScore();
        OffsetDateTime now = OffsetDateTime.now();

        LeaderboardEntryId entryId = LeaderboardEntryId.builder()
            .category(category)
            .playerId(playerId)
            .build();

        LeaderboardEntryEntity entity = leaderboardEntryRepository.findById(entryId).orElse(null);
        Integer previousRank = null;
        if (entity != null) {
            previousRank = safeLongToInt(leaderboardEntryRepository.countByIdCategoryAndScoreGreaterThan(category, entity.getScore()) + 1);
        } else {
            entity = LeaderboardEntryEntity.builder()
                .id(entryId)
                .playerName(null)
                .score(BigDecimal.ZERO)
                .scoreDisplay(null)
                .activeTitle(null)
                .guildId(null)
                .guildName(null)
                .guildTag(null)
                .updatedAt(now)
                .build();
        }

        entity.setScore(score);
        entity.setScoreDisplay(formatScoreDisplay(category, score));
        entity.setUpdatedAt(now);
        populateGuildMetadata(entity, playerId);
        leaderboardEntryRepository.save(entity);

        String seasonId = null;
        if (updateScoreRequest.getSeasonId() != null && updateScoreRequest.getSeasonId().isPresent()) {
            seasonId = updateScoreRequest.getSeasonId().orElse(null);
        }
        if (StringUtils.hasText(seasonId)) {
            upsertSeasonalEntry(seasonId, category, playerId, score, now, entity.getScoreDisplay());
        }

        int newRank = safeLongToInt(leaderboardEntryRepository.countByIdCategoryAndScoreGreaterThan(category, score) + 1);
        int rankChange = previousRank != null ? previousRank - newRank : 0;

        return new UpdateLeaderboardScore200Response()
            .newRank(newRank)
            .previousRank(previousRank)
            .rankChange(rankChange);
    }

    private PlayerAchievementEntity initializePlayerAchievement(UUID playerId, AchievementEntity achievement) {
        PlayerAchievementId id = PlayerAchievementId.builder()
            .playerId(playerId)
            .achievementId(achievement.getId())
            .build();
        PlayerAchievementEntity entity = PlayerAchievementEntity.builder()
            .id(id)
            .achievement(achievement)
            .build();
        entity.setTargetProgress(resolveTargetProgress(entity, achievement));
        return entity;
    }

    private Integer resolveTargetProgress(PlayerAchievementEntity entity, AchievementEntity achievement) {
        Integer existing = entity.getTargetProgress();
        if (existing != null && existing > 0) {
            return existing;
        }
        AchievementDefinitionRequirements requirements = readRequirements(achievement.getRequirements());
        if (requirements != null && requirements.getTargetValue() != null && requirements.getTargetValue() > 0) {
            return requirements.getTargetValue();
        }
        return existing;
    }

    private BigDecimal calculatePercentage(PlayerAchievementEntity entity) {
        Integer target = entity.getTargetProgress();
        if (target == null || target <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal value = BigDecimal.valueOf(entity.getCurrentProgress())
            .multiply(BigDecimal.valueOf(100))
            .divide(BigDecimal.valueOf(target), 2, RoundingMode.HALF_UP);
        if (value.compareTo(BigDecimal.valueOf(100)) > 0) {
            return BigDecimal.valueOf(100);
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        return value;
    }

    private boolean isUnlocked(PlayerAchievementEntity entity) {
        Integer target = entity.getTargetProgress();
        if (target == null || target <= 0) {
            return false;
        }
        return entity.getCurrentProgress() >= target;
    }

    private Specification<AchievementEntity> buildAchievementSpecification(String category, String rarity, Boolean unlocked) {
        Specification<AchievementEntity> specification = Specification.where(null);
        if (StringUtils.hasText(category)) {
            String normalized = category.trim().toUpperCase(Locale.ROOT);
            specification = specification.and((root, query, builder) -> builder.equal(builder.upper(root.get("category")), normalized));
        }
        if (StringUtils.hasText(rarity)) {
            String normalized = rarity.trim().toUpperCase(Locale.ROOT);
            specification = specification.and((root, query, builder) -> builder.equal(builder.upper(root.get("rarity")), normalized));
        }
        if (unlocked != null) {
            if (Boolean.TRUE.equals(unlocked)) {
                specification = specification.and((root, query, builder) -> builder.isFalse(root.get("hidden")));
            } else {
                specification = specification.and((root, query, builder) -> builder.isTrue(root.get("hidden")));
            }
        }
        return specification;
    }

    private Page<PlayerAchievementEntity> fetchPlayerAchievements(UUID playerId, String status, Pageable pageable) {
        if (!StringUtils.hasText(status)) {
            return playerAchievementRepository.findByIdPlayerId(playerId, pageable);
        }
        String normalized = status.trim().toUpperCase(Locale.ROOT);
        boolean valid = Arrays.stream(StatusEnum.values()).anyMatch(value -> value.getValue().equals(normalized));
        if (!valid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status value");
        }
        return playerAchievementRepository.findByIdPlayerIdAndStatus(playerId, normalized, pageable);
    }

    private PlayerAchievement toPlayerAchievement(PlayerAchievementEntity entity) {
        AchievementDefinition definition = toAchievementDefinition(entity.getAchievement());
        PlayerAchievementProgress progress = toProgress(entity);
        PlayerAchievement dto = new PlayerAchievement()
            .achievement(definition)
            .progress(progress)
            .updatedAt(entity.getUpdatedAt());
        String status = entity.getStatus();
        if (StringUtils.hasText(status)) {
            try {
                dto.status(StatusEnum.fromValue(status));
            } catch (IllegalArgumentException ex) {
                log.warn("Unknown achievement status {} for player {}", status, entity.getId().getPlayerId());
            }
        }
        if (entity.getUnlockedAt() != null) {
            dto.unlockedAt(entity.getUnlockedAt());
        }
        return dto;
    }

    private PlayerAchievementProgress toProgress(PlayerAchievementEntity entity) {
        Float percentage = entity.getProgressPercent() != null ? entity.getProgressPercent().floatValue() : null;
        return new PlayerAchievementProgress()
            .current(entity.getCurrentProgress())
            .target(entity.getTargetProgress())
            .percentage(percentage);
    }

    private Title toTitle(PlayerTitleEntity entity) {
        Title title = new Title()
            .id(entity.getId().getTitleId())
            .name(entity.getName())
            .displayName(entity.getDisplayName())
            .color(entity.getColor())
            .unlockedAt(entity.getUnlockedAt());
        String rarity = entity.getRarity();
        if (StringUtils.hasText(rarity)) {
            try {
                title.rarity(Title.RarityEnum.fromValue(rarity.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException ex) {
                log.warn("Unknown title rarity {} for player {}", rarity, entity.getId().getPlayerId());
            }
        }
        return title;
    }

    private AchievementDefinition toAchievementDefinition(AchievementEntity entity) {
        AchievementDefinition definition = new AchievementDefinition()
            .id(entity.getId())
            .name(entity.getName())
            .description(entity.getDescription())
            .points(entity.getPoints())
            .isHidden(entity.isHidden())
            .createdAt(entity.getCreatedAt());
        if (StringUtils.hasText(entity.getCategory())) {
            try {
                definition.category(CategoryEnum.fromValue(entity.getCategory().toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException ex) {
                log.warn("Unknown category {} for achievement {}", entity.getCategory(), entity.getId());
            }
        }
        if (StringUtils.hasText(entity.getRarity())) {
            try {
                definition.rarity(RarityEnum.fromValue(entity.getRarity().toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException ex) {
                log.warn("Unknown rarity {} for achievement {}", entity.getRarity(), entity.getId());
            }
        }
        if (StringUtils.hasText(entity.getIcon())) {
            try {
                definition.icon(URI.create(entity.getIcon()));
            } catch (IllegalArgumentException ex) {
                log.warn("Invalid icon URI {} for achievement {}", entity.getIcon(), entity.getId());
            }
        }
        definition.requirements(readRequirements(entity.getRequirements()));
        definition.rewards(readRewards(entity.getRewards()));
        return definition;
    }

    private AchievementDefinitionRequirements readRequirements(String json) {
        return readJson(json, AchievementDefinitionRequirements.class);
    }

    private AchievementRewards readRewards(String json) {
        return readJson(json, AchievementRewards.class);
    }

    private <T> T readJson(String json, Class<T> type) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException ex) {
            log.warn("Failed to parse JSON for {}", type.getSimpleName(), ex);
            return null;
        }
    }

    private PaginationMeta toPaginationMeta(Page<?> page) {
        return new PaginationMeta()
            .page(page.getNumber() + 1)
            .pageSize(page.getSize())
            .total(Math.toIntExact(page.getTotalElements()))
            .totalPages(page.getTotalPages())
            .hasNext(page.hasNext())
            .hasPrev(page.hasPrevious());
    }

    private Pageable buildPageable(Integer page, Integer size, Sort sort) {
        int resolvedPage = page != null && page > 0 ? page : DEFAULT_PAGE;
        int resolvedSize = size != null && size > 0 ? size : DEFAULT_PAGE_SIZE;
        return PageRequest.of(resolvedPage - 1, resolvedSize, sort);
    }

    private int resolveLimit(Integer value) {
        return (value != null && value > 0) ? value : DEFAULT_LEADERBOARD_LIMIT;
    }

    private long resolveOffset(Integer value) {
        return (value != null && value >= 0) ? value.longValue() : 0L;
    }

    private Set<UUID> resolveFriendIds(UUID playerId) {
        String key = playerId.toString();
        return friendshipRepository.findAcceptedFriendships(key).stream()
            .map(friendship -> extractFriendId(friendship, key))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toSet());
    }

    private Optional<UUID> extractFriendId(FriendshipEntity friendship, String playerKey) {
        String candidate = playerKey.equalsIgnoreCase(friendship.getCharacterId1())
            ? friendship.getCharacterId2()
            : friendship.getCharacterId1();
        try {
            return Optional.of(UUID.fromString(candidate));
        } catch (IllegalArgumentException ex) {
            log.warn("Invalid friend identifier stored: {}", candidate);
            return Optional.empty();
        }
    }

    private List<LeaderboardEntry> mapLeaderboardEntries(List<LeaderboardEntryEntity> entities, long offset) {
        List<LeaderboardEntry> result = new ArrayList<>(entities.size());
        for (int i = 0; i < entities.size(); i++) {
            result.add(toLeaderboardEntry(entities.get(i), (int) (offset + i + 1)));
        }
        return result;
    }

    private List<LeaderboardEntry> mapSeasonalEntries(List<SeasonalLeaderboardEntryEntity> entities, long offset) {
        List<LeaderboardEntry> result = new ArrayList<>(entities.size());
        for (int i = 0; i < entities.size(); i++) {
            SeasonalLeaderboardEntryEntity entity = entities.get(i);
            LeaderboardEntry entry = new LeaderboardEntry()
                .rank((int) (offset + i + 1))
                .playerId(entity.getId().getPlayerId())
                .score(entity.getScore())
                .scoreDisplay(entity.getScoreDisplay())
                .updatedAt(entity.getUpdatedAt());
            result.add(entry);
        }
        return result;
    }

    private List<GuildLeaderboardEntry> mapGuildLeaderboardEntries(List<GuildLeaderboardEntryEntity> entities, long offset) {
        List<GuildLeaderboardEntry> result = new ArrayList<>(entities.size());
        for (int i = 0; i < entities.size(); i++) {
            GuildLeaderboardEntryEntity entity = entities.get(i);
            GuildLeaderboardEntry entry = new GuildLeaderboardEntry()
                .rank((int) (offset + i + 1))
                .guildId(entity.getId().getGuildId())
                .guildName(entity.getGuildName())
                .guildTag(entity.getGuildTag())
                .score(entity.getScore())
                .scoreDisplay(entity.getScoreDisplay())
                .memberCount(entity.getMemberCount())
                .leaderName(entity.getLeaderName())
                .updatedAt(entity.getUpdatedAt());
            result.add(entry);
        }
        return result;
    }

    private LeaderboardEntry toLeaderboardEntry(LeaderboardEntryEntity entity, int rank) {
        LeaderboardEntry entry = new LeaderboardEntry()
            .rank(rank)
            .playerId(entity.getId().getPlayerId())
            .playerName(entity.getPlayerName())
            .score(entity.getScore())
            .scoreDisplay(entity.getScoreDisplay())
            .updatedAt(entity.getUpdatedAt());
        if (StringUtils.hasText(entity.getActiveTitle())) {
            entry.activeTitle(entity.getActiveTitle());
        }
        if (entity.getGuildId() != null) {
            LeaderboardEntryGuild guild = new LeaderboardEntryGuild()
                .guildId(entity.getGuildId())
                .guildName(entity.getGuildName())
                .guildTag(entity.getGuildTag());
            entry.guild(guild);
        }
        return entry;
    }

    private OffsetDateTime resolveLatestUpdatedAt(Collection<?> entities) {
        return entities.stream()
            .map(obj -> {
                if (obj instanceof LeaderboardEntryEntity entry) {
                    return entry.getUpdatedAt();
                }
                if (obj instanceof GuildLeaderboardEntryEntity guild) {
                    return guild.getUpdatedAt();
                }
                if (obj instanceof SeasonalLeaderboardEntryEntity seasonal) {
                    return seasonal.getUpdatedAt();
                }
                return null;
            })
            .filter(Objects::nonNull)
            .max(OffsetDateTime::compareTo)
            .orElse(null);
    }

    private int safeLongToInt(long value) {
        if (value > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (value < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) value;
    }

    private Float calculatePercentile(long rank, long total) {
        if (total <= 0) {
            return null;
        }
        double percentile = 100.0 - ((rank - 1) * 100.0 / total);
        percentile = Math.max(0, Math.min(100, percentile));
        return (float) percentile;
    }

    private List<LeaderboardEntry> resolveNearbyPlayerEntries(String category, int rank, long total) {
        if (total == 0) {
            return List.of();
        }
        long start = Math.max(rank - DEFAULT_NEARBY_WINDOW, 1) - 1;
        int limit = Math.min(DEFAULT_NEARBY_WINDOW * 2 + 1, safeLongToInt(total - start));
        Pageable pageable = new OffsetBasedPageRequest(start, Math.max(limit, 1), Sort.by(Sort.Direction.DESC, "score").and(Sort.by(Sort.Direction.ASC, "updatedAt")));
        List<LeaderboardEntryEntity> slice = leaderboardEntryRepository.findByIdCategory(category, pageable).getContent();
        return mapLeaderboardEntries(slice, pageable.getOffset());
    }

    private List<GuildLeaderboardEntry> resolveNearbyGuildEntries(String category, int rank, long total) {
        if (total == 0) {
            return List.of();
        }
        long start = Math.max(rank - DEFAULT_NEARBY_WINDOW, 1) - 1;
        int limit = Math.min(DEFAULT_NEARBY_WINDOW * 2 + 1, safeLongToInt(total - start));
        Pageable pageable = new OffsetBasedPageRequest(start, Math.max(limit, 1), Sort.by(Sort.Direction.DESC, "score").and(Sort.by(Sort.Direction.ASC, "updatedAt")));
        List<GuildLeaderboardEntryEntity> slice = guildLeaderboardEntryRepository.findByIdCategory(category, pageable).getContent();
        return mapGuildLeaderboardEntries(slice, pageable.getOffset());
    }

    private void populateGuildMetadata(LeaderboardEntryEntity entity, UUID playerId) {
        guildMemberRepository.findByIdCharacterId(playerId).ifPresent(member -> {
            entity.setGuildId(member.getId().getGuildId());
            guildRepository.findById(member.getId().getGuildId()).ifPresent(guild -> {
                entity.setGuildName(guild.getName());
                entity.setGuildTag(guild.getTag());
            });
        });
    }

    private void upsertSeasonalEntry(String seasonId, String category, UUID playerId, BigDecimal score, OffsetDateTime updatedAt, String scoreDisplay) {
        ensureSeasonExists(seasonId);
        SeasonalLeaderboardEntryId id = SeasonalLeaderboardEntryId.builder()
            .seasonId(seasonId)
            .category(category)
            .playerId(playerId)
            .build();

        SeasonalLeaderboardEntryEntity entity = seasonalLeaderboardEntryRepository.findById(id).orElse(
            SeasonalLeaderboardEntryEntity.builder()
                .id(id)
                .score(BigDecimal.ZERO)
                .updatedAt(updatedAt)
                .build()
        );

        entity.setScore(score);
        entity.setScoreDisplay(scoreDisplay);
        entity.setUpdatedAt(updatedAt);
        seasonalLeaderboardEntryRepository.save(entity);
    }

    private void ensureSeasonExists(String seasonId) {
        if (leaderboardSeasonRepository.existsById(seasonId)) {
            return;
        }
        LeaderboardSeasonEntity season = LeaderboardSeasonEntity.builder()
            .seasonId(seasonId)
            .name(seasonId)
            .current(false)
            .build();
        leaderboardSeasonRepository.save(season);
    }

    private Season toSeason(LeaderboardSeasonEntity entity) {
        return new Season()
            .seasonId(entity.getSeasonId())
            .name(entity.getName())
            .startDate(entity.getStartDate())
            .endDate(entity.getEndDate())
            .isCurrent(entity.isCurrent());
    }

    private String formatScoreDisplay(String category, BigDecimal score) {
        if (score == null) {
            return null;
        }
        String normalized = category != null ? category.toUpperCase(Locale.ROOT) : "";
        String numeric = score.stripTrailingZeros().toPlainString();
        return switch (normalized) {
            case "LEVEL" -> "Level " + numeric;
            case "WEALTH" -> numeric + " eddies";
            case "PVP_RATING" -> numeric + " rating";
            case "ACHIEVEMENTS" -> numeric + " points";
            default -> numeric;
        };
    }
}

