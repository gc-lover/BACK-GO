package handlers

import (
	"net/http"

	"github.com/gin-gonic/gin"
	"go.uber.org/zap"
)

// RegisterRoutes регистрирует все маршруты API
func RegisterRoutes(router *gin.RouterGroup, logger *zap.Logger) {
	// Health check для API v1
	router.GET("/health", func(c *gin.Context) {
		c.JSON(http.StatusOK, gin.H{
			"status":  "ok",
			"message": "NECPGAME backend API v1 is running",
			"version": "1.0.0",
		})
	})

	// Группа маршрутов для игровых данных
	gameplay := router.Group("/gameplay")
	{
		// Health check для игровых данных
		gameplay.GET("/health", func(c *gin.Context) {
			c.JSON(http.StatusOK, gin.H{
				"status":  "ok",
				"message": "Gameplay API is running",
			})
		})

		// Пример эндпоинта
		gameplay.GET("/status", func(c *gin.Context) {
			c.JSON(http.StatusOK, gin.H{
				"message": "Gameplay endpoints are ready",
				"version": "1.0.0",
			})
		})
	}

	// Социальные функции
	social := router.Group("/social")
	{
		social.GET("/health", func(c *gin.Context) {
			c.JSON(http.StatusOK, gin.H{
				"status":  "ok",
				"message": "Social API is running",
			})
		})
	}

	logger.Info("Routes registered successfully")
}
