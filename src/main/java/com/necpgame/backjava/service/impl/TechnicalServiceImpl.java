package com.necpgame.backjava.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.necpgame.backjava.entity.NotificationEntity;
import com.necpgame.backjava.model.GetNotifications200Response;
import com.necpgame.backjava.model.MarkAllNotificationsReadRequest;
import com.necpgame.backjava.model.Notification;
import com.necpgame.backjava.model.PaginationMeta;
import com.necpgame.backjava.model.SendNotification200Response;
import com.necpgame.backjava.model.SendNotificationRequest;
import com.necpgame.backjava.repository.NotificationRepository;
import com.necpgame.backjava.service.TechnicalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TechnicalServiceImpl implements TechnicalService {

    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public GetNotifications200Response getNotifications(String playerId, Boolean unreadOnly, String type, Integer page, Integer limit) {
        UUID playerUuid = parseUuid(playerId);
        if (playerUuid == null) {
            log.warn("getNotifications called with invalid player_id: {}", playerId);
            return emptyNotificationsResponse();
        }
        int pageNumber = page == null || page < 1 ? 0 : page - 1;
        int pageSize = limit == null || limit < 1 ? 50 : limit;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        boolean onlyUnread = Boolean.TRUE.equals(unreadOnly);
        String normalizedType = type == null ? null : type.trim().toLowerCase(Locale.ROOT);
        boolean hasType = normalizedType != null && !normalizedType.isBlank();

        Page<NotificationEntity> pageResult;
        if (hasType && normalizedType != null && onlyUnread) {
            pageResult = notificationRepository.findByPlayerIdAndTypeIgnoreCaseAndIsReadFalse(playerUuid, normalizedType, pageable);
        } else if (hasType && normalizedType != null) {
            pageResult = notificationRepository.findByPlayerIdAndTypeIgnoreCase(playerUuid, normalizedType, pageable);
        } else if (onlyUnread) {
            pageResult = notificationRepository.findByPlayerIdAndIsReadFalse(playerUuid, pageable);
        } else {
            pageResult = notificationRepository.findByPlayerId(playerUuid, pageable);
        }

        List<Notification> models = new ArrayList<>();
        for (NotificationEntity entity : pageResult.getContent()) {
            models.add(toModel(entity));
        }

        PaginationMeta pagination = new PaginationMeta(
            pageResult.getNumber() + 1,
            pageResult.getSize(),
            toInt(pageResult.getTotalElements()),
            pageResult.getTotalPages()
        );
        pagination.setHasNext(pageResult.hasNext());
        pagination.setHasPrev(pageResult.hasPrevious());

        return new GetNotifications200Response()
            .notifications(models)
            .pagination(pagination);
    }

    @Override
    public Object markAllNotificationsRead(MarkAllNotificationsReadRequest markAllNotificationsReadRequest) {
        UUID playerUuid = markAllNotificationsReadRequest != null ? parseUuid(markAllNotificationsReadRequest.getPlayerId()) : null;
        if (playerUuid == null) {
            log.warn("markAllNotificationsRead called with invalid player_id");
            return Map.of("updated", 0);
        }
        List<NotificationEntity> unread = notificationRepository.findByPlayerIdAndIsReadFalse(playerUuid);
        int updated = 0;
        for (NotificationEntity entity : unread) {
            if (!entity.isRead()) {
                entity.setRead(true);
                updated++;
            }
        }
        if (!unread.isEmpty()) {
            notificationRepository.saveAll(unread);
        }
        return Map.of("updated", updated);
    }

    @Override
    public Object markNotificationRead(String notificationId) {
        UUID id = parseUuid(notificationId);
        if (id == null) {
            log.warn("markNotificationRead called with invalid notification_id: {}", notificationId);
            return Map.of("updated", false);
        }
        Optional<NotificationEntity> notificationOpt = notificationRepository.findById(id);
        if (notificationOpt.isEmpty()) {
            return Map.of("updated", false);
        }
        NotificationEntity entity = notificationOpt.get();
        if (!entity.isRead()) {
            entity.setRead(true);
            notificationRepository.save(entity);
            return Map.of("updated", true);
        }
        return Map.of("updated", false);
    }

    @Override
    @SuppressWarnings({"DataFlowIssue", "null"})
    public SendNotification200Response sendNotification(SendNotificationRequest sendNotificationRequest) {
        UUID playerUuid = parseUuid(sendNotificationRequest.getPlayerId());
        if (playerUuid == null) {
            throw new IllegalArgumentException("Invalid player_id supplied");
        }
        NotificationEntity entity = NotificationEntity.builder()
            .playerId(playerUuid)
            .type(sendNotificationRequest.getType() != null ? sendNotificationRequest.getType().getValue() : null)
            .priority(sendNotificationRequest.getPriority() != null ? sendNotificationRequest.getPriority().getValue() : SendNotificationRequest.PriorityEnum.NORMAL.getValue())
            .message(sendNotificationRequest.getMessage())
            .data(serializeData(sendNotificationRequest.getData()))
            .sendEmail(Boolean.TRUE.equals(sendNotificationRequest.getSendEmail()))
            .read(false)
            .build();
        notificationRepository.save(entity);
        String notificationId = entity.getId() != null ? entity.getId().toString() : null;
        return new SendNotification200Response().notificationId(notificationId);
    }

    private Notification toModel(NotificationEntity entity) {
        Notification notification = new Notification()
            .notificationId(entity.getId() != null ? entity.getId().toString() : null)
            .playerId(entity.getPlayerId() != null ? entity.getPlayerId().toString() : null)
            .message(entity.getMessage())
            .isRead(entity.isRead())
            .createdAt(entity.getCreatedAt());

        if (entity.getType() != null) {
            try {
                notification.setType(Notification.TypeEnum.fromValue(entity.getType().toLowerCase(Locale.ROOT)));
            } catch (IllegalArgumentException ex) {
                log.warn("Unknown notification type stored: {}", entity.getType());
            }
        }
        if (entity.getPriority() != null) {
            try {
                notification.setPriority(Notification.PriorityEnum.fromValue(entity.getPriority().toLowerCase(Locale.ROOT)));
            } catch (IllegalArgumentException ex) {
                log.warn("Unknown notification priority stored: {}", entity.getPriority());
            }
        }
        if (entity.getData() != null && !entity.getData().isBlank()) {
            notification.setData(deserializeData(entity.getData()));
        }
        return notification;
    }

    private String serializeData(Object data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException ex) {
            log.warn("Failed to serialize notification data, storing as string: {}", ex.getMessage());
            return data.toString();
        }
    }

    private Object deserializeData(String data) {
        try {
            return objectMapper.readValue(data, Object.class);
        } catch (JsonProcessingException ex) {
            log.warn("Failed to deserialize notification data, returning raw string: {}", ex.getMessage());
            return data;
        }
    }

    private UUID parseUuid(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private int toInt(long value) {
        return value > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) value;
    }

    private GetNotifications200Response emptyNotificationsResponse() {
        PaginationMeta pagination = new PaginationMeta(1, 0, 0, 0);
        pagination.setHasNext(false);
        pagination.setHasPrev(false);
        return new GetNotifications200Response()
            .notifications(new ArrayList<>())
            .pagination(pagination);
    }
}

