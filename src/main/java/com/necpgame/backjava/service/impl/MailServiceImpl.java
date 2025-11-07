package com.necpgame.backjava.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.necpgame.backjava.entity.MailMessageEntity;
import com.necpgame.backjava.model.ClaimAttachmentsRequest;
import com.necpgame.backjava.model.GetInbox200Response;
import com.necpgame.backjava.model.MailMessage;
import com.necpgame.backjava.model.PaginationMeta;
import com.necpgame.backjava.model.SendMail200Response;
import com.necpgame.backjava.model.SendMailRequest;
import com.necpgame.backjava.repository.MailMessageRepository;
import com.necpgame.backjava.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
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
public class MailServiceImpl implements MailService {

    private final MailMessageRepository mailMessageRepository;
    private final ObjectMapper objectMapper;

    @Override
    public Object claimAttachments(String mailId, ClaimAttachmentsRequest claimAttachmentsRequest) {
        Optional<MailMessageEntity> entityOpt = findActiveMessage(mailId);
        if (entityOpt.isEmpty()) {
            log.warn("Mail message not found for claim: {}", mailId);
            return Map.of("claimed", false);
        }
        MailMessageEntity entity = entityOpt.get();
        if (!entity.isClaimed()) {
            entity.setClaimed(true);
            entity.setClaimedAt(OffsetDateTime.now());
            mailMessageRepository.save(entity);
        }
        return Map.of("claimed", true);
    }

    @Override
    public Object deleteMail(String mailId) {
        Optional<MailMessageEntity> entityOpt = findActiveMessage(mailId);
        if (entityOpt.isEmpty()) {
            log.warn("Mail message not found for delete: {}", mailId);
            return Map.of("deleted", false);
        }
        MailMessageEntity entity = entityOpt.get();
        if (!entity.isDeleted()) {
            entity.setDeleted(true);
            entity.setDeletedAt(OffsetDateTime.now());
            mailMessageRepository.save(entity);
        }
        return Map.of("deleted", true);
    }

    @Override
    @Transactional(readOnly = true)
    public GetInbox200Response getInbox(String characterId, Integer page, Integer limit, Boolean unreadOnly) {
        UUID recipientId = parseUuid(characterId);
        int pageIndex = Math.max(0, page == null ? 0 : page - 1);
        int pageSize = Math.max(1, limit == null ? 50 : limit);
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.DESC, "sentAt"));
        Page<MailMessageEntity> mailPage = mailMessageRepository.findByRecipientCharacterIdAndDeletedFalse(recipientId, pageable);
        List<MailMessageEntity> content = mailPage.getContent();
        if (Boolean.TRUE.equals(unreadOnly)) {
            List<MailMessageEntity> filtered = new ArrayList<>();
            for (MailMessageEntity entity : content) {
                if (!entity.isRead()) {
                    filtered.add(entity);
                }
            }
            content = filtered;
        }
        List<MailMessage> messages = new ArrayList<>();
        for (MailMessageEntity entity : content) {
            messages.add(toMailMessage(entity));
        }
        PaginationMeta pagination = new PaginationMeta(
            pageIndex + 1,
            pageSize,
            toInt(mailPage.getTotalElements()),
            mailPage.getTotalPages()
        );
        pagination.setHasNext(mailPage.hasNext());
        pagination.setHasPrev(mailPage.hasPrevious());
        return new GetInbox200Response()
            .mail(messages)
            .pagination(pagination);
    }

    @Override
    public Object readMail(String mailId) {
        Optional<MailMessageEntity> entityOpt = findActiveMessage(mailId);
        if (entityOpt.isEmpty()) {
            log.warn("Mail message not found for read: {}", mailId);
            return Map.of("read", false);
        }
        MailMessageEntity entity = entityOpt.get();
        if (!entity.isRead()) {
            entity.setRead(true);
            entity.setReadAt(OffsetDateTime.now());
            mailMessageRepository.save(entity);
        }
        return Map.of("read", true);
    }

    @Override
    public SendMail200Response sendMail(SendMailRequest sendMailRequest) {
        MailMessageEntity entity = new MailMessageEntity();
        entity.setSenderCharacterId(parseUuid(sendMailRequest.getSenderCharacterId()));
        entity.setRecipientCharacterId(resolveRecipientId(sendMailRequest.getReceiverCharacterName()));
        entity.setRecipientCharacterName(sendMailRequest.getReceiverCharacterName());
        entity.setMailType("PLAYER");
        entity.setSubject(sendMailRequest.getSubject());
        entity.setBody(sendMailRequest.getBody());
        entity.setAttachedItems(serializeAttachments(sendMailRequest));
        entity.setAttachedGold(toLong(sendMailRequest.getGold()));
        entity.setCodAmount(toLong(sendMailRequest.getCodAmount()));
        entity.setExpiresAt(OffsetDateTime.now().plusDays(30));
        MailMessageEntity saved = mailMessageRepository.save(entity);
        log.info("Mail sent: {}", saved.getId());
        return new SendMail200Response().mailId(String.valueOf(saved.getId()));
    }

    private Optional<MailMessageEntity> findActiveMessage(String mailId) {
        try {
            Long id = Long.parseLong(mailId);
            return mailMessageRepository.findByIdAndDeletedFalse(id);
        } catch (NumberFormatException ex) {
            log.warn("Invalid mail id format: {}", mailId);
            return Optional.empty();
        }
    }

    private UUID parseUuid(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid UUID: " + value);
        }
    }

    private UUID resolveRecipientId(String recipientName) {
        if (recipientName == null || recipientName.isBlank()) {
            throw new IllegalArgumentException("Recipient name is required");
        }
        return UUID.nameUUIDFromBytes(recipientName.trim().toLowerCase(Locale.ROOT).getBytes(StandardCharsets.UTF_8));
    }

    private String serializeAttachments(SendMailRequest request) {
        if (request.getAttachments() == null || request.getAttachments().isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(request.getAttachments());
        } catch (JsonProcessingException ex) {
            log.error("Failed to serialize attachments", ex);
            return "[]";
        }
    }

    private long toLong(BigDecimal value) {
        if (value == null) {
            return 0L;
        }
        return value.longValue();
    }

    private int toInt(long value) {
        if (value > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) value;
    }

    private MailMessage toMailMessage(MailMessageEntity entity) {
        BigDecimal goldAmount = entity.getAttachedGold() == null ? null : BigDecimal.valueOf(entity.getAttachedGold());
        BigDecimal codValue = entity.getCodAmount() == null ? null : BigDecimal.valueOf(entity.getCodAmount());
        boolean hasAttachments = goldAmount != null && goldAmount.longValue() > 0;
        if (entity.getAttachedItems() != null && !entity.getAttachedItems().isBlank() && !"[]".equals(entity.getAttachedItems().trim())) {
            hasAttachments = true;
        }
        MailMessage mailMessage = new MailMessage()
            .mailId(String.valueOf(entity.getId()))
            .senderName(entity.getSenderCharacterId() == null ? "SYSTEM" : entity.getSenderCharacterId().toString())
            .subject(entity.getSubject())
            .body(entity.getBody())
            .hasAttachments(hasAttachments)
            .goldAttached(goldAmount)
            .codAmount(codValue)
            .isRead(entity.isRead())
            .isSystem("SYSTEM".equalsIgnoreCase(entity.getMailType()))
            .createdAt(entity.getSentAt())
            .expiresAt(entity.getExpiresAt());
        return mailMessage;
    }
}

