package com.arekalov.papersplease.repository

import com.arekalov.papersplease.model.entity.Notification
import com.arekalov.papersplease.model.enums.NotificationType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface NotificationRepository : JpaRepository<Notification, UUID> {
    fun findByUser_IdOrderByCreatedAtDesc(userId: UUID, pageable: Pageable): Page<Notification>

    fun findByUser_IdAndIsReadOrderByCreatedAtDesc(
        userId: UUID,
        isRead: Boolean,
        pageable: Pageable,
    ): Page<Notification>

    fun findByNotificationType(type: NotificationType, pageable: Pageable): Page<Notification>

    fun findByShift_Id(shiftId: UUID): List<Notification>

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId")
    fun markAllAsReadByUserId(@Param("userId") userId: UUID)

    fun countByUser_IdAndIsRead(userId: UUID, isRead: Boolean): Long
}
