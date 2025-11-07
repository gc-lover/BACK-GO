package com.necpgame.backjava.service;

import com.necpgame.backjava.model.GetNotifications200Response;
import com.necpgame.backjava.model.MarkAllNotificationsReadRequest;
import org.springframework.lang.Nullable;
import com.necpgame.backjava.model.SendNotification200Response;
import com.necpgame.backjava.model.SendNotificationRequest;
import org.springframework.validation.annotation.Validated;

/**
 * Service interface for TechnicalService.
 * Generated from OpenAPI specification.
 * 
 * This is a service interface that should be implemented by a service implementation class.
 */
@Validated
public interface TechnicalService {

    /**
     * GET /technical/notifications : Получить уведомления
     * Возвращает список уведомлений. Поддерживает фильтрацию и пагинацию. 
     *
     * @param playerId  (required)
     * @param unreadOnly  (optional, default to false)
     * @param type  (optional)
     * @param page  (optional, default to 1)
     * @param limit  (optional, default to 50)
     * @return GetNotifications200Response
     */
    GetNotifications200Response getNotifications(String playerId, Boolean unreadOnly, String type, Integer page, Integer limit);

    /**
     * POST /technical/notifications/mark-all-read : Отметить все как прочитанные
     * Отмечает все уведомления как прочитанные
     *
     * @param markAllNotificationsReadRequest  (required)
     * @return Object
     */
    Object markAllNotificationsRead(MarkAllNotificationsReadRequest markAllNotificationsReadRequest);

    /**
     * POST /technical/notifications/{notification_id}/mark-read : Отметить как прочитанное
     * Отмечает уведомление как прочитанное
     *
     * @param notificationId  (required)
     * @return Object
     */
    Object markNotificationRead(String notificationId);

    /**
     * POST /technical/notifications/send : Отправить уведомление
     * Отправляет уведомление игроку. Используется системой для различных событий. 
     *
     * @param sendNotificationRequest  (required)
     * @return SendNotification200Response
     */
    SendNotification200Response sendNotification(SendNotificationRequest sendNotificationRequest);
}

