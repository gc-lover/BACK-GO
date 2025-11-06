package com.necpgame.backjava.service;

import com.necpgame.backjava.model.BlockPlayerRequest;
import com.necpgame.backjava.model.CreatePartyRequest;
import com.necpgame.backjava.model.GetFriends200Response;
import com.necpgame.backjava.model.InviteToPartyRequest;
import com.necpgame.backjava.model.JoinPartyRequest;
import com.necpgame.backjava.model.Party;
import com.necpgame.backjava.model.PartyDetails;
import com.necpgame.backjava.model.SendFriendRequestRequest;
import org.springframework.validation.annotation.Validated;

/**
 * Service interface for SocialService.
 * Generated from OpenAPI specification.
 * 
 * This is a service interface that should be implemented by a service implementation class.
 */
@Validated
public interface SocialService {

    /**
     * POST /social/party/create : Создать группу
     * Создает новую группу. Создатель становится лидером. 
     *
     * @param createPartyRequest  (required)
     * @return Party
     */
    Party createParty(CreatePartyRequest createPartyRequest);

    /**
     * GET /social/party/{party_id} : Получить информацию о группе
     * Возвращает детали группы, участников, настройки
     *
     * @param partyId  (required)
     * @return PartyDetails
     */
    PartyDetails getParty(String partyId);

    /**
     * POST /social/party/{party_id}/invite : Пригласить в группу
     * Отправляет приглашение в группу. Только лидер может приглашать. 
     *
     * @param partyId  (required)
     * @param inviteToPartyRequest  (required)
     * @return Object
     */
    Object inviteToParty(String partyId, InviteToPartyRequest inviteToPartyRequest);

    /**
     * POST /social/party/{party_id}/join : Присоединиться к группе
     * Принимает приглашение и присоединяется к группе
     *
     * @param partyId  (required)
     * @param joinPartyRequest  (required)
     * @return Party
     */
    Party joinParty(String partyId, JoinPartyRequest joinPartyRequest);

    /**
     * POST /social/party/{party_id}/leave : Покинуть группу
     * Покидает группу. Если лидер - лидерство передается другому. 
     *
     * @param partyId  (required)
     * @param joinPartyRequest  (required)
     * @return Object
     */
    Object leaveParty(String partyId, JoinPartyRequest joinPartyRequest);

    /**
     * POST /social/friends/request/{request_id}/accept : Принять запрос в друзья
     * Принимает запрос, добавляет в friends list
     *
     * @param requestId  (required)
     * @return Object
     */
    Object acceptFriendRequest(String requestId);

    /**
     * POST /social/friends/block : Заблокировать игрока
     * Добавляет игрока в block list. Блокирует сообщения, invites, trade. 
     *
     * @param blockPlayerRequest  (required)
     * @return Object
     */
    Object blockPlayer(BlockPlayerRequest blockPlayerRequest);

    /**
     * GET /social/friends : Получить список друзей
     * Возвращает список друзей с online статусом
     *
     * @param playerId  (required)
     * @return GetFriends200Response
     */
    GetFriends200Response getFriends(String playerId);

    /**
     * DELETE /social/friends/{friend_id}/remove : Удалить из друзей
     * Удаляет игрока из списка друзей
     *
     * @param friendId  (required)
     * @return Object
     */
    Object removeFriend(String friendId);

    /**
     * POST /social/friends/request : Отправить запрос в друзья
     * Отправляет запрос на добавление в друзья
     *
     * @param sendFriendRequestRequest  (required)
     * @return Object
     */
    Object sendFriendRequest(SendFriendRequestRequest sendFriendRequestRequest);
}

