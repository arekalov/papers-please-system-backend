@file:Suppress("TooManyFunctions")

package com.arekalov.papersplease.mapper

import com.arekalov.papersplease.dto.appeal.AppealRequest
import com.arekalov.papersplease.dto.appeal.AppealResponse
import com.arekalov.papersplease.dto.document.DocumentRequest
import com.arekalov.papersplease.dto.document.DocumentResponse
import com.arekalov.papersplease.dto.event.EventRequest
import com.arekalov.papersplease.dto.event.EventResponse
import com.arekalov.papersplease.dto.notification.NotificationRequest
import com.arekalov.papersplease.dto.notification.NotificationResponse
import com.arekalov.papersplease.dto.participation.ParticipationRequest
import com.arekalov.papersplease.dto.participation.ParticipationResponse
import com.arekalov.papersplease.dto.shift.ShiftResponse
import com.arekalov.papersplease.dto.ticket.TicketRequest
import com.arekalov.papersplease.dto.ticket.TicketResponse
import com.arekalov.papersplease.dto.upk.UpkRequest
import com.arekalov.papersplease.dto.upk.UpkResponse
import com.arekalov.papersplease.dto.user.UserRequest
import com.arekalov.papersplease.dto.user.UserResponse
import com.arekalov.papersplease.model.entity.Appeal
import com.arekalov.papersplease.model.entity.Document
import com.arekalov.papersplease.model.entity.Event
import com.arekalov.papersplease.model.entity.Notification
import com.arekalov.papersplease.model.entity.Participation
import com.arekalov.papersplease.model.entity.Shift
import com.arekalov.papersplease.model.entity.Ticket
import com.arekalov.papersplease.model.entity.Upk
import com.arekalov.papersplease.model.entity.User
import com.arekalov.papersplease.model.enums.AppealDecision
import com.arekalov.papersplease.model.enums.Priority
import com.arekalov.papersplease.model.enums.TicketStatus
import java.time.Instant

fun User.toResponse() = UserResponse(
    id = id.toString(),
    name = name,
    email = email,
    role = role,
    upkId = upk?.id?.toString(),
)

fun UserRequest.toEntity(upk: Upk?) = User(
    name = name,
    email = email,
    passwordHash = "",
    role = role,
    upk = upk,
)

fun Upk.toResponse() = UpkResponse(
    id = id.toString(),
    name = name,
    region = region,
)

fun UpkRequest.toEntity() = Upk(
    name = name,
    region = region,
)

fun Shift.toResponse() = ShiftResponse(
    id = id.toString(),
    startTime = startTime,
    endTime = endTime,
    createdBy = createdBy.id.toString(),
    upkId = upk.id.toString(),
)

fun Participation.toResponse() = ParticipationResponse(
    id = id.toString(),
    userId = user.id.toString(),
    shiftId = shift.id.toString(),
    coeffBonus = bonusCoefficient ?: 1.0f,
    coeffPenalty = penaltyCoefficient ?: 1.0f,
    specialization = specialization,
)

fun ParticipationRequest.toEntity(shift: Shift, user: User) = Participation(
    shift = shift,
    user = user,
    specialization = specialization,
    bonusCoefficient = coeffBonus,
    penaltyCoefficient = coeffPenalty,
)

fun Event.toResponse() = EventResponse(
    id = id.toString(),
    shiftId = shift.id.toString(),
    description = description,
    priority = Priority.NORMAL,
)

fun EventRequest.toEntity(shift: Shift) = Event(
    shift = shift,
    time = Instant.now(),
    description = description,
)

fun Ticket.toResponse() = TicketResponse(
    id = id.toString(),
    ticketType = ticketType,
    status = status,
    priority = priority,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deadlineAt = deadlineAt,
    resolvedAt = null,
    authorId = author.id.toString(),
    executorId = executor?.id?.toString(),
    parentTicketId = null,
)

fun TicketRequest.toEntity(
    author: User,
    executor: User?,
    shift: Shift?,
) = Ticket(
    ticketType = ticketType,
    status = status,
    priority = priority,
    deadlineAt = deadlineAt,
    author = author,
    executor = executor,
    shift = shift,
    description = "",
    resolution = null,
)

fun Document.toResponse() = DocumentResponse(
    id = id.toString(),
    userId = "",
    documentType = documentType,
    body = emptyMap(),
    validFrom = issuedAt,
    validUntil = expiresAt,
)

fun DocumentRequest.toEntity() = Document(
    documentType = documentType,
    body = "",
    issuedAt = validFrom ?: Instant.now(),
    expiresAt = validUntil,
)

fun Notification.toResponse() = NotificationResponse(
    id = id.toString(),
    userId = user.id.toString(),
    relatedTicketId = null,
    message = message,
    notificationType = notificationType,
    sentAt = createdAt,
    isRead = false,
)

fun NotificationRequest.toEntity(user: User) = Notification(
    user = user,
    notificationType = notificationType,
    message = message,
)

fun Appeal.toResponse() = AppealResponse(
    id = id.toString(),
    ticketId = ticket.id.toString(),
    createdBy = createdBy.id.toString(),
    status = TicketStatus.OPEN,
    verdict = decision ?: AppealDecision.REJECTED,
    comment = reason,
    createdAt = createdAt,
    checkedBy = decidedBy?.id?.toString(),
    checkedAt = decidedAt,
)

fun AppealRequest.toEntity(ticket: Ticket, createdBy: User) = Appeal(
    ticket = ticket,
    createdBy = createdBy,
    reason = comment.orEmpty(),
    decision = null,
    decidedBy = null,
    decidedAt = null,
    decisionNotes = null,
)
