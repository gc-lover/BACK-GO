package com.necpgame.backjava.api;

import com.necpgame.backjava.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Game Initial State API - контракт для получения начального состояния игры.
 * 
 * Сгенерировано из: API-SWAGGER/api/v1/game/initial-state.yaml
 * OpenAPI Generator version: 7.2.0
 * 
 * НЕ редактируйте этот файл вручную - он генерируется автоматически!
 */
@Validated
@Tag(name = "Initial State", description = "Начальное состояние игры")
public interface GameInitialStateApi {

    /**
     * GET /v1/game/initial-state : Получить начальное состояние игры
     * 
     * @param characterId ID персонажа (required)
     * @return Начальное состояние игры (status code 200)
     *         or Not Found (status code 404)
     *         or Internal Server Error (status code 500)
     */
    @Operation(
        operationId = "getInitialState",
        summary = "Получить начальное состояние игры",
        description = "Возвращает начальное состояние игры для персонажа.",
        tags = { "Initial State" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Начальное состояние игры", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = InitialStateResponse.class))
            }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = com.necpgame.backjava.model.Error.class))
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = com.necpgame.backjava.model.Error.class))
            })
        },
        security = {
            @SecurityRequirement(name = "BearerAuth")
        }
    )
    @GetMapping(
        value = "/v1/game/initial-state",
        produces = { "application/json" }
    )
    default ResponseEntity<InitialStateResponse> getInitialState(
        @Parameter(name = "characterId", description = "ID персонажа", required = true)
        @Valid @RequestParam(value = "characterId", required = true) UUID characterId
    ) {
        return ResponseEntity.ok().build();
    }

    /**
     * GET /v1/game/tutorial-steps : Получить шаги туториала
     * 
     * @param characterId ID персонажа (required)
     * @return Шаги туториала (status code 200)
     *         or Not Found (status code 404)
     *         or Internal Server Error (status code 500)
     */
    @Operation(
        operationId = "getTutorialSteps",
        summary = "Получить шаги туториала",
        description = "Возвращает список шагов туториала для нового игрока.",
        tags = { "Initial State" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Шаги туториала", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = TutorialStepsResponse.class))
            }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = com.necpgame.backjava.model.Error.class))
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = com.necpgame.backjava.model.Error.class))
            })
        },
        security = {
            @SecurityRequirement(name = "BearerAuth")
        }
    )
    @GetMapping(
        value = "/v1/game/tutorial-steps",
        produces = { "application/json" }
    )
    default ResponseEntity<TutorialStepsResponse> getTutorialSteps(
        @Parameter(name = "characterId", description = "ID персонажа", required = true)
        @Valid @RequestParam(value = "characterId", required = true) UUID characterId
    ) {
        return ResponseEntity.ok().build();
    }
}

