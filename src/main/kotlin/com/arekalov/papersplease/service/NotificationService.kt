package com.arekalov.papersplease.service

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.notification.NotificationRequest
import com.arekalov.papersplease.dto.notification.NotificationRequestPartial
import com.arekalov.papersplease.dto.notification.NotificationResponse
import com.arekalov.papersplease.exception.ResourceNotFoundException
import com.arekalov.papersplease.mapper.toEntity
import com.arekalov.papersplease.mapper.toResponse
import com.arekalov.papersplease.model.enums.NotificationType
import com.arekalov.papersplease.repository.NotificationRepository
import com.arekalov.papersplease.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository,
) {

    @Transactional(readOnly = true)
    fun getAll(limit: Int, offset: Int): PagedResponse<NotificationResponse> {
        val pageable = PageRequest.of(offset / limit, limit)
        val page = notificationRepository.findAll(pageable)

        return PagedResponse(
            items = page.content.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional(readOnly = true)
    fun getById(id: String): NotificationResponse {
        val notification = notificationRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Notification with id $id not found") }
        return notification.toResponse()
    }

    @Transactional(readOnly = true)
    fun getByUser(userId: String, limit: Int, offset: Int): PagedResponse<NotificationResponse> {
        val pageable = PageRequest.of(offset / limit, limit)
        val page = notificationRepository.findByUser_IdOrderByCreatedAtDesc(UUID.fromString(userId), pageable)

        return PagedResponse(
            items = page.content.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional(readOnly = true)
    fun getByType(type: NotificationType, limit: Int, offset: Int): PagedResponse<NotificationResponse> {
        val pageable = PageRequest.of(offset / limit, limit)
        val page = notificationRepository.findByNotificationType(type, pageable)

        return PagedResponse(
            items = page.content.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional
    fun create(request: NotificationRequest): NotificationResponse {
        val user = userRepository.findById(UUID.fromString(request.userId))
            .orElseThrow { ResourceNotFoundException("User with id ${request.userId} not found") }

        val notification = request.toEntity(user)

        return notificationRepository.save(notification).toResponse()
    }

    @Transactional
    fun update(id: String, request: NotificationRequest): NotificationResponse {
        val notification = notificationRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Notification with id $id not found") }

        val user = userRepository.findById(UUID.fromString(request.userId))
            .orElseThrow { ResourceNotFoundException("User with id ${request.userId} not found") }

        notification.apply {
            this.user = user
            notificationType = request.notificationType
            message = request.message
        }

        return notificationRepository.save(notification).toResponse()
    }

    @Transactional
    fun partialUpdate(id: String, request: NotificationRequestPartial): NotificationResponse {
        val notification = notificationRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Notification with id $id not found") }

        request.userId?.let { userId ->
            notification.user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow { ResourceNotFoundException("User with id $userId not found") }
        }
        request.notificationType?.let { notification.notificationType = it }
        request.message?.let { notification.message = it }

        return notificationRepository.save(notification).toResponse()
    }

    @Transactional
    fun delete(id: String) {
        val notification = notificationRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Notification with id $id not found") }
        notificationRepository.delete(notification)
    }
}
