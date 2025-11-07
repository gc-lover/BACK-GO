package com.necpgame.backjava.service.impl;

import com.necpgame.backjava.entity.LoreCharacterCategoryEntity;
import com.necpgame.backjava.entity.LoreCodexEntryEntity;
import com.necpgame.backjava.entity.LoreCodexProgressEntity;
import com.necpgame.backjava.entity.LoreFactionEntity;
import com.necpgame.backjava.entity.LoreLocationEntity;
import com.necpgame.backjava.entity.LoreTimelineEventEntity;
import com.necpgame.backjava.entity.LoreUniverseEntity;
import com.necpgame.backjava.entity.enums.LoreFactionType;
import com.necpgame.backjava.entity.enums.LoreLocationType;
import com.necpgame.backjava.entity.enums.LoreSearchCategory;
import com.necpgame.backjava.entity.enums.LoreSearchResultType;
import com.necpgame.backjava.entity.enums.TimelineEventType;
import com.necpgame.backjava.model.GetCharacterCodex200Response;
import com.necpgame.backjava.model.GetTimeline200Response;
import com.necpgame.backjava.model.LoreSearchResult;
import com.necpgame.backjava.model.SearchLore200Response;
import com.necpgame.backjava.model.UniverseLore;
import com.necpgame.backjava.model.UnlockCodexEntryRequest;
import com.necpgame.backjava.repository.LoreCharacterCategoryRepository;
import com.necpgame.backjava.repository.LoreCodexEntryRepository;
import com.necpgame.backjava.repository.LoreCodexProgressRepository;
import com.necpgame.backjava.repository.LoreFactionRepository;
import com.necpgame.backjava.repository.LoreLocationRepository;
import com.necpgame.backjava.repository.LoreTimelineEventRepository;
import com.necpgame.backjava.repository.LoreUniverseRepository;
import com.necpgame.backjava.service.UniverseService;
import com.necpgame.backjava.service.mapper.LoreMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class UniverseServiceImpl implements UniverseService {

    private static final int MAX_SEARCH_RESULTS = 30;

    private final LoreUniverseRepository universeRepository;
    private final LoreTimelineEventRepository timelineEventRepository;
    private final LoreCodexEntryRepository codexEntryRepository;
    private final LoreCodexProgressRepository codexProgressRepository;
    private final LoreFactionRepository factionRepository;
    private final LoreLocationRepository locationRepository;
    private final LoreCharacterCategoryRepository characterCategoryRepository;
    private final LoreMapper loreMapper;

    public UniverseServiceImpl(
            LoreUniverseRepository universeRepository,
            LoreTimelineEventRepository timelineEventRepository,
            LoreCodexEntryRepository codexEntryRepository,
            LoreCodexProgressRepository codexProgressRepository,
            LoreFactionRepository factionRepository,
            LoreLocationRepository locationRepository,
            LoreCharacterCategoryRepository characterCategoryRepository,
            LoreMapper loreMapper
    ) {
        this.universeRepository = universeRepository;
        this.timelineEventRepository = timelineEventRepository;
        this.codexEntryRepository = codexEntryRepository;
        this.codexProgressRepository = codexProgressRepository;
        this.factionRepository = factionRepository;
        this.locationRepository = locationRepository;
        this.characterCategoryRepository = characterCategoryRepository;
        this.loreMapper = loreMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public GetCharacterCodex200Response getCharacterCodex(UUID characterId) {
        if (characterId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "character_id is required");
        }

        List<LoreCodexEntryEntity> entries = codexEntryRepository.findAll();
        Map<UUID, LoreCodexProgressEntity> progressByEntryId = codexProgressRepository.findByCharacterId(characterId)
                .stream()
                .collect(Collectors.toMap(progress -> progress.getEntry().getId(), progress -> progress));

        List<com.necpgame.backjava.model.CodexEntry> mappedEntries = entries.stream()
                .map(entry -> {
                    LoreCodexProgressEntity progress = progressByEntryId.get(entry.getId());
                    boolean unlocked = progress != null && progress.isUnlocked();
                    return loreMapper.toCodexEntry(entry, unlocked);
                })
                .collect(Collectors.toList());

        long unlockedCount = mappedEntries.stream()
                .filter(com.necpgame.backjava.model.CodexEntry::getUnlocked)
                .count();
        BigDecimal completion = entries.isEmpty()
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(unlockedCount)
                        .divide(BigDecimal.valueOf(entries.size()), 2, RoundingMode.HALF_UP);

        GetCharacterCodex200Response response = new GetCharacterCodex200Response();
        response.setEntries(mappedEntries);
        response.setCompletionPercentage(completion);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public GetTimeline200Response getTimeline(String era, String eventType) {
        List<LoreTimelineEventEntity> events;

        boolean hasEra = StringUtils.hasText(era);
        boolean hasType = StringUtils.hasText(eventType);

        TimelineEventType timelineEventType = null;
        if (hasType) {
            timelineEventType = parseEventType(eventType);
        }

        if (hasEra && hasType) {
            events = timelineEventRepository.findByEraIgnoreCaseAndTypeOrderByYearAsc(era, timelineEventType);
        } else if (hasEra) {
            events = timelineEventRepository.findByEraIgnoreCaseOrderByYearAsc(era);
        } else if (hasType) {
            events = timelineEventRepository.findByTypeOrderByYearAsc(timelineEventType);
        } else {
            events = timelineEventRepository.findAllByOrderByYearAsc();
        }

        GetTimeline200Response response = new GetTimeline200Response();
        response.setEvents(loreMapper.toTimelineEvents(events));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public UniverseLore getUniverseLore() {
        LoreUniverseEntity entity = universeRepository.findTopByOrderByCreatedAtDesc()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Universe lore not found"));
        return loreMapper.toUniverseLore(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public SearchLore200Response searchLore(String q, String category) {
        if (!StringUtils.hasText(q)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "q is required");
        }

        LoreSearchCategory searchCategory;
        try {
            searchCategory = LoreSearchCategory.fromValue(category);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported category: " + category);
        }

        String query = q.trim();
        String normalized = query.toLowerCase(Locale.ROOT);

        List<LoreSearchResult> results = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        Consumer<LoreSearchResult> collector = result -> {
            String key = result.getResultType() + "::" + result.getId();
            if (seen.add(key)) {
                results.add(result);
            }
        };

        EnumSet<LoreSearchCategory> categories = searchCategory == LoreSearchCategory.ALL
                ? EnumSet.of(LoreSearchCategory.FACTIONS, LoreSearchCategory.LOCATIONS, LoreSearchCategory.CHARACTERS, LoreSearchCategory.UNIVERSE)
                : EnumSet.of(searchCategory);

        if (categories.contains(LoreSearchCategory.FACTIONS)) {
            collectFactions(normalized, collector);
        }
        if (categories.contains(LoreSearchCategory.LOCATIONS)) {
            collectLocations(normalized, collector);
        }
        if (categories.contains(LoreSearchCategory.CHARACTERS)) {
            collectCharacterCategories(normalized, collector);
        }
        if (categories.contains(LoreSearchCategory.UNIVERSE)) {
            collectTimelineEvents(normalized, collector);
        }

        if (results.size() > MAX_SEARCH_RESULTS) {
            results = results.subList(0, MAX_SEARCH_RESULTS);
        }

        SearchLore200Response response = new SearchLore200Response();
        response.setResults(results);
        return response;
    }

    @Override
    public Void unlockCodexEntry(UnlockCodexEntryRequest request) {
        if (request == null || request.getCharacterId() == null || !StringUtils.hasText(request.getEntryId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "character_id and entry_id are required");
        }

        LoreCodexEntryEntity entry = codexEntryRepository.findByEntryId(request.getEntryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Codex entry not found"));

        Optional<LoreCodexProgressEntity> current = codexProgressRepository.findByCharacterIdAndEntry(request.getCharacterId(), entry);
        if (current.isPresent()) {
            LoreCodexProgressEntity progress = current.get();
            if (!progress.isUnlocked()) {
                progress.setUnlocked(true);
                progress.setUnlockedAt(OffsetDateTime.now());
                codexProgressRepository.save(progress);
            }
            return null;
        }

        LoreCodexProgressEntity progress = LoreCodexProgressEntity.builder()
                .id(UUID.randomUUID())
                .characterId(request.getCharacterId())
                .entry(entry)
                .unlocked(true)
                .unlockedAt(OffsetDateTime.now())
                .build();
        codexProgressRepository.save(progress);
        return null;
    }

    private TimelineEventType parseEventType(String raw) {
        try {
            return TimelineEventType.fromValue(raw);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported event_type: " + raw);
        }
    }

    private void collectFactions(String normalizedQuery, Consumer<LoreSearchResult> collector) {
        List<LoreFactionEntity> byName = factionRepository.findTop10ByNameContainingIgnoreCase(normalizedQuery);
        List<LoreFactionEntity> byDescription = factionRepository.findTop10ByDescriptionShortContainingIgnoreCase(normalizedQuery);
        List<LoreFactionEntity> combined = new ArrayList<>(byName);
        combined.addAll(byDescription);

        for (LoreFactionEntity entity : combined) {
            double score = computeScore(entity.getName(), entity.getDescriptionShort(), normalizedQuery);
            collector.accept(loreMapper.toSearchResult(
                    LoreSearchResultType.FACTION,
                    entity.getExternalId(),
                    entity.getName(),
                    entity.getDescriptionShort(),
                    score));
        }
    }

    private void collectLocations(String normalizedQuery, Consumer<LoreSearchResult> collector) {
        List<LoreLocationEntity> byName = locationRepository.findTop10ByNameContainingIgnoreCase(normalizedQuery);
        List<LoreLocationEntity> byDescription = locationRepository.findTop10ByDescriptionShortContainingIgnoreCase(normalizedQuery);
        List<LoreLocationEntity> combined = new ArrayList<>(byName);
        combined.addAll(byDescription);

        for (LoreLocationEntity entity : combined) {
            double score = computeScore(entity.getName(), entity.getDescriptionShort(), normalizedQuery);
            collector.accept(loreMapper.toSearchResult(
                    LoreSearchResultType.LOCATION,
                    entity.getExternalId(),
                    entity.getName(),
                    entity.getDescriptionShort(),
                    score));
        }
    }

    private void collectCharacterCategories(String normalizedQuery, Consumer<LoreSearchResult> collector) {
        characterCategoryRepository.findTop10ByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(normalizedQuery, normalizedQuery)
                .forEach(entity -> {
                    double score = computeScore(entity.getName(), entity.getDescription(), normalizedQuery);
                    collector.accept(loreMapper.toSearchResult(
                            LoreSearchResultType.CHARACTER,
                            entity.getCategoryId(),
                            entity.getName(),
                            entity.getDescription(),
                            score));
                });
    }

    private void collectTimelineEvents(String normalizedQuery, Consumer<LoreSearchResult> collector) {
        timelineEventRepository.findTop10ByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(normalizedQuery, normalizedQuery)
                .forEach(entity -> {
                    double score = computeScore(entity.getName(), entity.getDescription(), normalizedQuery);
                    collector.accept(loreMapper.toSearchResult(
                            LoreSearchResultType.TIMELINE,
                            entity.getEventId(),
                            entity.getName(),
                            entity.getDescription(),
                            score));
                });
    }

    private double computeScore(String name, String description, String query) {
        String lowerName = name == null ? "" : name.toLowerCase(Locale.ROOT);
        String lowerDescription = description == null ? "" : description.toLowerCase(Locale.ROOT);
        if (lowerName.contains(query)) {
            return 1.0d;
        }
        if (lowerDescription.contains(query)) {
            return 0.8d;
        }
        return 0.5d;
    }
}
