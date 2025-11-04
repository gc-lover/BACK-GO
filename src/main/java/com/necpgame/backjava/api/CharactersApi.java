package com.necpgame.backjava.api;

import com.necpgame.backjava.model.CreateCharacter201Response;
import com.necpgame.backjava.model.CreateCharacterRequest;
import com.necpgame.backjava.model.DeleteCharacter200Response;
import com.necpgame.backjava.model.Error;
import com.necpgame.backjava.model.GetCharacterClasses200Response;
import com.necpgame.backjava.model.GetCharacterOrigins200Response;
import com.necpgame.backjava.model.ListCharacters200Response;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;

/**
 * Service interface for CharactersApi.
 * Generated from OpenAPI specification.
 * 
 * This is a service interface that should be implemented by a service implementation class.
 */
@Validated
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.17.0")public interface CharactersApi {

    /**
     * POST /characters : Создание нового персонажа
     * Создает нового персонажа для текущего аккаунта. Проверяет лимит персонажей и доступность фракции/города.
     *
     * @param createCharacterRequest  (required)
     * @return CreateCharacter201Response
     */
    CreateCharacter201Response createCharacter(CreateCharacterRequest createCharacterRequest);

    /**
     * DELETE /characters/{character_id} : Удаление персонажа
     * Удаляет персонажа текущего аккаунта. Необратимое удаление всех данных.
     *
     * @param characterId Идентификатор персонажа для удаления (required)
     * @return DeleteCharacter200Response
     */
    DeleteCharacter200Response deleteCharacter(UUID characterId);

    /**
     * GET /characters/classes : Список доступных классов
     * Получает список всех доступных классов персонажей с описаниями.
     *
     * @return GetCharacterClasses200Response
     */
    GetCharacterClasses200Response getCharacterClasses();

    /**
     * GET /characters/origins : Список доступных происхождений
     * Получает список всех доступных происхождений с описаниями и доступными фракциями.
     *
     * @return GetCharacterOrigins200Response
     */
    GetCharacterOrigins200Response getCharacterOrigins();

    /**
     * GET /characters : Список персонажей игрока
     * Получает список всех персонажей текущего аккаунта с краткой информацией.
     *
     * @return ListCharacters200Response
     */
    ListCharacters200Response listCharacters();
}

