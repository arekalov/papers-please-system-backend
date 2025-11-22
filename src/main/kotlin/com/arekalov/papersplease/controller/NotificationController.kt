package com.arekalov.papersplease.controller

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.notification.NotificationRequest
import com.arekalov.papersplease.dto.notification.NotificationRequestPartial
import com.arekalov.papersplease.dto.notification.NotificationResponse
import com.arekalov.papersplease.model.enums.NotificationType
import com.arekalov.papersplease.service.NotificationService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/notifications")
@PreAuthorize("isAuthenticated()")
class NotificationController(
    private val notificationService: NotificationService,
) {

    @GetMapping
    suspend fun getAllNotifications(
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<NotificationResponse>> {
        val response = notificationService.getAll(limit, offset)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    suspend fun getNotificationById(
        @PathVariable id: String,
    ): ResponseEntity<NotificationResponse> {
        val response = notificationService.getById(id)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/by-user/{userId}")
    suspend fun getNotificationsByUser(
        @PathVariable userId: String,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<NotificationResponse>> {
        val response = notificationService.getByUser(userId, limit, offset)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/by-type/{type}")
    suspend fun getNotificationsByType(
        @PathVariable type: NotificationType,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<NotificationResponse>> {
        val response = notificationService.getByType(type, limit, offset)
        return ResponseEntity.ok(response)
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('BOSS', 'SECURITY', 'GOD')")
    suspend fun createNotification(
        @Valid @RequestBody request: NotificationRequest,
    ): ResponseEntity<NotificationResponse> {
        val response = notificationService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('BOSS', 'SECURITY', 'GOD')")
    suspend fun updateNotification(
        @PathVariable id: String,
        @Valid @RequestBody request: NotificationRequest,
    ): ResponseEntity<NotificationResponse> {
        val response = notificationService.update(id, request)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{id}")
    suspend fun partialUpdateNotification(
        @PathVariable id: String,
        @Valid @RequestBody request: NotificationRequestPartial,
    ): ResponseEntity<NotificationResponse> {
        val response = notificationService.partialUpdate(id, request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    suspend fun deleteNotification(
        @PathVariable id: String,
    ): ResponseEntity<Void> {
        notificationService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
