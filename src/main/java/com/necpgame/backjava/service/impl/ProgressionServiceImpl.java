package com.necpgame.backjava.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.necpgame.backjava.entity.AchievementEntity;
import com.necpgame.backjava.entity.PlayerAchievementEntity;
import com.necpgame.backjava.entity.PlayerAchievementId;
import com.necpgame.backjava.entity.PlayerTitleEntity;
import com.necpgame.backjava.model.AchievementDefinition;
import com.necpgame.backjava.model.AchievementDefinition.CategoryEnum;
import com.necpgame.backjava.model.AchievementDefinition.RarityEnum;
import com.necpgame.backjava.model.AchievementDefinitionRequirements;
import com.necpgame.backjava.model.AchievementRewards;
import com.necpgame.backjava.model.GetPlayerAchievements200Response;
import com.necpgame.backjava.model.GetPlayerTitles200Response;
import com.necpgame.backjava.model.ListAchievements200Response;
import com.necpgame.backjava.model.PaginationMeta;
import com.necpgame.backjava.model.PlayerAchievement;
import com.necpgame.backjava.model.PlayerAchievement.StatusEnum;
import com.necpgame.backjava.model.PlayerAchievementProgress;
import com.necpgame.backjava.model.ProgressUpdateResult;
import com.necpgame.backjava.model.SetActiveTitleRequest;
import com.necpgame.backjava.model.Title;
import com.necpgame.backjava.model.UpdateProgressRequest;
import com.necpgame.backjava.repository.AchievementRepository;
import com.necpgame.backjava.repository.PlayerAchievementRepository;
import com.necpgame.backjava.repository.PlayerTitleRepository;
import com.necpgame.backjava.service.ProgressionService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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

    private final AchievementRepository achievementRepository;
    private final PlayerAchievementRepository playerAchievementRepository;
    private final PlayerTitleRepository playerTitleRepository;
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
}

