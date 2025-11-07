package com.necpgame.backjava.service;

import com.necpgame.backjava.model.BlockCreateRequest;
import com.necpgame.backjava.model.BlockListResponse;
import com.necpgame.backjava.model.Error;
import com.necpgame.backjava.model.FriendError;
import com.necpgame.backjava.model.FriendListResponse;
import com.necpgame.backjava.model.FriendRequestCreate;
import com.necpgame.backjava.model.FriendRequestDecline;
import com.necpgame.backjava.model.FriendRequestsResponse;
import org.springframework.lang.Nullable;
import com.necpgame.backjava.model.PresenceFeedResponse;
import com.necpgame.backjava.model.PresenceUpdateRequest;
import com.necpgame.backjava.model.RecentPlayersResponse;
import com.necpgame.backjava.model.SocialInviteRequest;
import com.necpgame.backjava.model.SuggestionsResponse;
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
     * DELETE /social/friends/blocks/{blockId} : Снять блокировку
     *
     * @param blockId Идентификатор записи блокировки (required)
     * @return Void
     */
    Void socialFriendsBlocksBlockIdDelete(String blockId);

    /**
     * GET /social/friends/blocks : Список блокировок и игнора
     *
     * @return BlockListResponse
     */
    BlockListResponse socialFriendsBlocksGet();

    /**
     * POST /social/friends/blocks : Добавить игрока в блок или mute
     *
     * @param blockCreateRequest  (required)
     * @return Void
     */
    Void socialFriendsBlocksPost(BlockCreateRequest blockCreateRequest);

    /**
     * DELETE /social/friends/{friendId} : Удалить игрока из друзей
     *
     * @param friendId Идентификатор дружеской связи/игрока (required)
     * @return Void
     */
    Void socialFriendsFriendIdDelete(String friendId);

    /**
     * GET /social/friends : Список друзей текущего игрока
     *
     * @param includePresence  (optional, default to true)
     * @param includeMetadata  (optional, default to false)
     * @return FriendListResponse
     */
    FriendListResponse socialFriendsGet(Boolean includePresence, Boolean includeMetadata);

    /**
     * POST /social/friends/invites : Отправить социальное приглашение (party/guild)
     *
     * @param socialInviteRequest  (required)
     * @return Void
     */
    Void socialFriendsInvitesPost(SocialInviteRequest socialInviteRequest);

    /**
     * POST /social/friends : Отправить запрос в друзья
     *
     * @param friendRequestCreate  (required)
     * @return Void
     */
    Void socialFriendsPost(FriendRequestCreate friendRequestCreate);

    /**
     * GET /social/friends/presence : Получить актуальные статусы присутствия друзей
     *
     * @return PresenceFeedResponse
     */
    PresenceFeedResponse socialFriendsPresenceGet();

    /**
     * POST /social/friends/presence : Обновить собственный статус присутствия
     *
     * @param presenceUpdateRequest  (required)
     * @return Void
     */
    Void socialFriendsPresencePost(PresenceUpdateRequest presenceUpdateRequest);

    /**
     * GET /social/friends/recent : Недавние игроки
     *
     * @return RecentPlayersResponse
     */
    RecentPlayersResponse socialFriendsRecentGet();

    /**
     * GET /social/friends/requests : Получить входящие и исходящие запросы
     *
     * @return FriendRequestsResponse
     */
    FriendRequestsResponse socialFriendsRequestsGet();

    /**
     * POST /social/friends/requests/{requestId}/accept : Принять запрос
     *
     * @param requestId Идентификатор запроса в друзья (required)
     * @return Void
     */
    Void socialFriendsRequestsRequestIdAcceptPost(String requestId);

    /**
     * POST /social/friends/requests/{requestId}/decline : Отклонить запрос
     *
     * @param requestId Идентификатор запроса в друзья (required)
     * @param friendRequestDecline  (optional)
     * @return Void
     */
    Void socialFriendsRequestsRequestIdDeclinePost(String requestId, FriendRequestDecline friendRequestDecline);

    /**
     * GET /social/friends/suggestions : Рекомендации друзей
     *
     * @param source  (optional)
     * @return SuggestionsResponse
     */
    SuggestionsResponse socialFriendsSuggestionsGet(String source);
}

