package com.necpgame.backjava.service;

import com.necpgame.backjava.model.ClaimAttachmentsRequest;
import com.necpgame.backjava.model.GetInbox200Response;
import org.springframework.lang.Nullable;
import com.necpgame.backjava.model.SendMail200Response;
import com.necpgame.backjava.model.SendMailRequest;
import org.springframework.validation.annotation.Validated;

/**
 * Service interface for MailService.
 * Generated from OpenAPI specification.
 * 
 * This is a service interface that should be implemented by a service implementation class.
 */
@Validated
public interface MailService {

    /**
     * POST /mail/{mail_id}/claim-attachments : Забрать вложения
     * Забирает items/gold из письма. Требует оплату COD (если установлен). 
     *
     * @param mailId  (required)
     * @param claimAttachmentsRequest  (optional)
     * @return Object
     */
    Object claimAttachments(String mailId, ClaimAttachmentsRequest claimAttachmentsRequest);

    /**
     * DELETE /mail/{mail_id}/delete : Удалить письмо
     * Удаляет письмо из inbox
     *
     * @param mailId  (required)
     * @return Object
     */
    Object deleteMail(String mailId);

    /**
     * GET /mail/inbox : Получить входящие письма
     * Возвращает список писем в inbox. Поддерживает пагинацию и фильтрацию. 
     *
     * @param characterId  (required)
     * @param page  (optional, default to 1)
     * @param limit  (optional, default to 50)
     * @param unreadOnly  (optional, default to false)
     * @return GetInbox200Response
     */
    GetInbox200Response getInbox(String characterId, Integer page, Integer limit, Boolean unreadOnly);

    /**
     * POST /mail/{mail_id}/read : Прочитать письмо
     * Отмечает письмо как прочитанное
     *
     * @param mailId  (required)
     * @return Object
     */
    Object readMail(String mailId);

    /**
     * POST /mail/send : Отправить письмо
     * Отправляет письмо другому игроку. Можно приложить items (до 10) и gold. Можно установить COD (оплата при получении). 
     *
     * @param sendMailRequest  (required)
     * @return SendMail200Response
     */
    SendMail200Response sendMail(SendMailRequest sendMailRequest);
}

