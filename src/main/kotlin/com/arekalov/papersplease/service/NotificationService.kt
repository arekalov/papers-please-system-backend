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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    suspend fun getAll(limit: Int, offset: Int): PagedResponse<NotificationResponse> = withContext(Dispatchers.IO) {
        val pageable = PageRequest.of(offset / limit, limit)
        val page = notificationRepository.findAll(pageable)

        PagedResponse(
            items = page.content.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional(readOnly = true)
    suspend fun getById(id: String): NotificationResponse = withContext(Dispatchers.IO) {
        val notification = notificationRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Notification with id $id not found") }
        notification.toResponse()
    }

    @Transactional(readOnly = true)
    suspend fun getByUser(userId: String, limit: Int, offset: Int): PagedResponse<NotificationResponse> =
        withContext(Dispatchers.IO) {
            val pageable = PageRequest.of(offset / limit, limit)
            val page = notificationRepository.findByUser_IdOrderByCreatedAtDesc(UUID.fromString(userId), pageable)

            PagedResponse(
                items = page.content.map { it.toResponse() },
                total = page.totalElements,
                limit = limit,
                offset = offset,
            )
        }

    @Transactional(readOnly = true)
    suspend fun getByType(type: NotificationType, limit: Int, offset: Int): PagedResponse<NotificationResponse> =
        withContext(Dispatchers.IO) {
            val pageable = PageRequest.of(offset / limit, limit)
            val page = notificationRepository.findByType(type, pageable)

            PagedResponse(
                items = page.content.map { it.toResponse() },
                total = page.totalElements,
                limit = limit,
                offset = offset,
            )
        }

    @Transactional
    suspend fun create(request: NotificationRequest): NotificationResponse = withContext(Dispatchers.IO) {
        val user = userRepository.findById(UUID.fromString(request.userId))
            .orElseThrow { ResourceNotFoundException("User with id ${request.userId} not found") }

        val notification = request.toEntity(user)

        notificationRepository.save(notification).toResponse()
    }

    @Transactional
    suspend fun update(id: String, request: NotificationRequest): NotificationResponse = withContext(Dispatchers.IO) {
        val notification = notificationRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Notification with id $id not found") }

        val user = userRepository.findById(UUID.fromString(request.userId))
            .orElseThrow { ResourceNotFoundException("User with id ${request.userId} not found") }

        notification.apply {
            this.user = user
            notificationType = request.notificationType
            message = request.message
        }

        notificationRepository.save(notification).toResponse()
    }

    @Transactional
    suspend fun partialUpdate(id: String, request: NotificationRequestPartial): NotificationResponse =
        withContext(Dispatchers.IO) {
            val notification = notificationRepository.findById(UUID.fromString(id))
                .orElseThrow { ResourceNotFoundException("Notification with id $id not found") }

            request.userId?.let { userId ->
                notification.user = userRepository.findById(UUID.fromString(userId))
                    .orElseThrow { ResourceNotFoundException("User with id $userId not found") }
            }
            request.notificationType?.let { notification.notificationType = it }
            request.message?.let { notification.message = it }

            notificationRepository.save(notification).toResponse()
        }

    @Transactional
    suspend fun delete(id: String) = withContext(Dispatchers.IO) {
        val notification = notificationRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Notification with id $id not found") }
        notificationRepository.delete(notification)
    }
}
