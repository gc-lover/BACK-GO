package com.necpgame.backjava.controller;

import com.necpgame.backjava.api.TechnicalApi;
import com.necpgame.backjava.model.GetNotifications200Response;
import com.necpgame.backjava.model.MarkAllNotificationsReadRequest;
import com.necpgame.backjava.model.SendNotification200Response;
import com.necpgame.backjava.model.SendNotificationRequest;
import com.necpgame.backjava.service.TechnicalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TechnicalController implements TechnicalApi {

    private final TechnicalService technicalService;

    @Override
    public ResponseEntity<GetNotifications200Response> getNotifications(String playerId, Boolean unreadOnly, @Nullable String type, Integer page, Integer limit) {
        return ResponseEntity.ok(technicalService.getNotifications(playerId, unreadOnly, type, page, limit));
    }

    @Override
    public ResponseEntity<Object> markAllNotificationsRead(MarkAllNotificationsReadRequest markAllNotificationsReadRequest) {
        return ResponseEntity.ok(technicalService.markAllNotificationsRead(markAllNotificationsReadRequest));
    }

    @Override
    public ResponseEntity<Object> markNotificationRead(String notificationId) {
        return ResponseEntity.ok(technicalService.markNotificationRead(notificationId));
    }

    @Override
    public ResponseEntity<SendNotification200Response> sendNotification(SendNotificationRequest sendNotificationRequest) {
        return ResponseEntity.ok(technicalService.sendNotification(sendNotificationRequest));
    }
}

