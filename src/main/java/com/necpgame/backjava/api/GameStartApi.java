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
 * Game Start API - контракт для запуска игры.
 * 
 * Сгенерировано из: API-SWAGGER/api/v1/game/start.yaml
 * OpenAPI Generator version: 7.2.0
 * 
 * НЕ редактируйте этот файл вручную - он генерируется автоматически!
 */
@Validated
@Tag(name = "Game Start", description = "Запуск игры и начальный контент")
public interface GameStartApi {

    /**
     * POST /v1/game/start : Начать игру
     * 
     * @param body  (required)
     * @return Игра началась успешно (status code 200)
     *         or Bad Request (status code 400)
     *         or Not Found (status code 404)
     *         or Conflict (status code 409)
     *         or Internal Server Error (status code 500)
     */
    @Operation(
        operationId = "startGame",
        summary = "Начать игру",
        description = "Начинает игру для созданного персонажа.",
        tags = { "Game Start" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Игра началась успешно", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = GameStartResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = com.necpgame.backjava.model.Error.class))
            }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = com.necpgame.backjava.model.Error.class))
            }),
            @ApiResponse(responseCode = "409", description = "Conflict", content = {
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
    @PostMapping(
        value = "/v1/game/start",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<GameStartResponse> startGame(
        @Parameter(name = "GameStartRequest", description = "", required = true)
        @Valid @RequestBody GameStartRequest body
    ) {
        return ResponseEntity.ok().build();
    }

    /**
     * GET /v1/game/welcome : Получить приветственный экран
     * 
     * @param characterId ID персонажа (required)
     * @return Приветственный экран (status code 200)
     *         or Not Found (status code 404)
     *         or Internal Server Error (status code 500)
     */
    @Operation(
        operationId = "getWelcomeScreen",
        summary = "Получить приветственный экран",
        description = "Возвращает приветственный экран для персонажа.",
        tags = { "Game Start" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Приветственный экран", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = WelcomeScreenResponse.class))
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
        value = "/v1/game/welcome",
        produces = { "application/json" }
    )
    default ResponseEntity<WelcomeScreenResponse> getWelcomeScreen(
        @Parameter(name = "characterId", description = "ID персонажа", required = true)
        @Valid @RequestParam(value = "characterId", required = true) UUID characterId
    ) {
        return ResponseEntity.ok().build();
    }

    /**
     * POST /v1/game/return : Вернуться в игру
     * 
     * @param body  (required)
     * @return Возврат в игру (status code 200)
     *         or Not Found (status code 404)
     *         or Internal Server Error (status code 500)
     */
    @Operation(
        operationId = "returnToGame",
        summary = "Вернуться в игру",
        description = "Возвращает игрока в игру при повторном входе.",
        tags = { "Game Start" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Возврат в игру", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = GameReturnResponse.class))
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
    @PostMapping(
        value = "/v1/game/return",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<GameReturnResponse> returnToGame(
        @Parameter(name = "GameReturnRequest", description = "", required = true)
        @Valid @RequestBody GameReturnRequest body
    ) {
        return ResponseEntity.ok().build();
    }
}

