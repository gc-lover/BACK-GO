package com.necpgame.backjava.controller;

import com.necpgame.backjava.api.MailApi;
import com.necpgame.backjava.model.ClaimAttachmentsRequest;
import com.necpgame.backjava.model.GetInbox200Response;
import com.necpgame.backjava.model.SendMail200Response;
import com.necpgame.backjava.model.SendMailRequest;
import com.necpgame.backjava.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MailController implements MailApi {

    private final MailService mailService;

    @Override
    public ResponseEntity<Object> claimAttachments(String mailId, @Nullable ClaimAttachmentsRequest claimAttachmentsRequest) {
        return ResponseEntity.ok(mailService.claimAttachments(mailId, claimAttachmentsRequest));
    }

    @Override
    public ResponseEntity<Object> deleteMail(String mailId) {
        return ResponseEntity.ok(mailService.deleteMail(mailId));
    }

    @Override
    public ResponseEntity<GetInbox200Response> getInbox(String characterId, Integer page, Integer limit, Boolean unreadOnly) {
        return ResponseEntity.ok(mailService.getInbox(characterId, page, limit, unreadOnly));
    }

    @Override
    public ResponseEntity<Object> readMail(String mailId) {
        return ResponseEntity.ok(mailService.readMail(mailId));
    }

    @Override
    public ResponseEntity<SendMail200Response> sendMail(SendMailRequest sendMailRequest) {
        return ResponseEntity.ok(mailService.sendMail(sendMailRequest));
    }
}

