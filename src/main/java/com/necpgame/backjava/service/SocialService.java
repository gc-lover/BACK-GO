package com.necpgame.backjava.service;

import com.necpgame.backjava.model.BlockPlayerRequest;
import com.necpgame.backjava.model.CreateGuildRequest;
import com.necpgame.backjava.model.CreatePartyRequest;
import com.necpgame.backjava.model.GetFriends200Response;
import com.necpgame.backjava.model.Guild;
import com.necpgame.backjava.model.GuildDetails;
import com.necpgame.backjava.model.InviteToGuildRequest;
import com.necpgame.backjava.model.InviteToPartyRequest;
import com.necpgame.backjava.model.JoinGuildRequest;
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
     * POST /social/guilds/create : Создать гильдию
     * Создает новую гильдию. Требует минимальный уровень и стоимость. 
     *
     * @param createGuildRequest  (required)
     * @return Guild
     */
    Guild createGuild(CreateGuildRequest createGuildRequest);

    /**
     * GET /social/guilds/{guild_id} : Получить информацию о гильдии
     * Возвращает детали гильдии, участников, звания
     *
     * @param guildId  (required)
     * @return GuildDetails
     */
    GuildDetails getGuild(String guildId);

    /**
     * POST /social/guilds/{guild_id}/invite : Пригласить в гильдию
     * Отправляет приглашение в гильдию. Требует право INVITE_MEMBERS. 
     *
     * @param guildId  (required)
     * @param inviteToGuildRequest  (required)
     * @return Object
     */
    Object inviteToGuild(String guildId, InviteToGuildRequest inviteToGuildRequest);

    /**
     * POST /social/guilds/{guild_id}/join : Вступить в гильдию
     * Принимает приглашение и вступает в гильдию
     *
     * @param guildId  (required)
     * @param joinGuildRequest  (required)
     * @return Object
     */
    Object joinGuild(String guildId, JoinGuildRequest joinGuildRequest);

    /**
     * POST /social/guilds/{guild_id}/leave : Покинуть гильдию
     * Покидает гильдию
     *
     * @param guildId  (required)
     * @param joinGuildRequest  (required)
     * @return Object
     */
    Object leaveGuild(String guildId, JoinGuildRequest joinGuildRequest);

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

