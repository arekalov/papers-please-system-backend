@file:Suppress("TooManyFunctions")

package com.arekalov.papersplease.mapper

import com.arekalov.papersplease.dto.document.DocumentRequest
import com.arekalov.papersplease.dto.document.DocumentResponse
import com.arekalov.papersplease.dto.event.EventResponse
import com.arekalov.papersplease.dto.notification.NotificationRequest
import com.arekalov.papersplease.dto.notification.NotificationResponse
import com.arekalov.papersplease.dto.participation.ParticipationRequest
import com.arekalov.papersplease.dto.participation.ParticipationResponse
import com.arekalov.papersplease.dto.shift.ShiftResponse
import com.arekalov.papersplease.dto.ticket.TicketDetailedResponse
import com.arekalov.papersplease.dto.ticket.TicketRequest
import com.arekalov.papersplease.dto.ticket.TicketResponse
import com.arekalov.papersplease.dto.upk.UpkRequest
import com.arekalov.papersplease.dto.upk.UpkResponse
import com.arekalov.papersplease.dto.user.UserRequest
import com.arekalov.papersplease.dto.user.UserResponse
import com.arekalov.papersplease.model.entity.Document
import com.arekalov.papersplease.model.entity.Event
import com.arekalov.papersplease.model.entity.Notification
import com.arekalov.papersplease.model.entity.Participation
import com.arekalov.papersplease.model.entity.Shift
import com.arekalov.papersplease.model.entity.Ticket
import com.arekalov.papersplease.model.entity.Upk
import com.arekalov.papersplease.model.entity.User
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

fun Participation.toResponse(totalResolvedTickets: Long = 0) = ParticipationResponse(
    id = id.toString(),
    userId = user.id.toString(),
    shiftId = shift.id.toString(),
    wage = wage,
    penalty = penalty,
    specialization = specialization,
    totalResolvedTickets = totalResolvedTickets,
)

fun ParticipationRequest.toEntity(shift: Shift, user: User) = Participation(
    shift = shift,
    user = user,
    specialization = specialization,
    wage = wage,
    penalty = penalty,
)

fun Event.toResponse() = EventResponse(
    id = id.toString(),
    time = time,
    description = description,
    specialization = specialization,
    priority = priority,
    upkId = upk.id.toString(),
)

fun Ticket.toResponse() = TicketResponse(
    id = id.toString(),
    ticketType = ticketType,
    status = status,
    priority = priority,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deadlineAt = deadlineAt,
    authorId = author.id.toString(),
    subjectId = subject.id.toString(),
    executor = executor?.toResponse(),
    shiftId = shift?.id?.toString(),
    description = description,
    resolution = resolution,
    appealDecision = appealDecision,
    relatedTicketIds = relatedTickets.mapNotNull { it.id?.toString() },
    documentIds = documents.mapNotNull { it.id?.toString() },
)

fun Ticket.toDetailedResponse() = TicketDetailedResponse(
    id = id.toString(),
    ticketType = ticketType,
    status = status,
    priority = priority,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deadlineAt = deadlineAt,
    authorId = author.id.toString(),
    subjectId = subject.id.toString(),
    executor = executor?.toResponse(),
    shiftId = shift?.id?.toString(),
    description = description,
    resolution = resolution,
    appealDecision = appealDecision,
    relatedTickets = relatedTickets.map { it.toResponse() },
    documents = documents.map { it.toResponse() },
)

fun TicketRequest.toEntity(
    author: User,
    subject: User,
    executor: User?,
    shift: Shift?,
) = Ticket(
    ticketType = ticketType,
    status = status,
    priority = priority,
    deadlineAt = deadlineAt,
    author = author,
    subject = subject,
    executor = executor,
    shift = shift,
    description = description,
    resolution = resolution,
)

fun Document.toResponse(): DocumentResponse {
    val bodyMap = try {
        com.fasterxml.jackson.module.kotlin.jacksonObjectMapper().readValue(
            body,
            object : com.fasterxml.jackson.core.type.TypeReference<Map<String, Any>>() {},
        )
    } catch (e: com.fasterxml.jackson.core.JsonProcessingException) {
        org.slf4j.LoggerFactory.getLogger(Document::class.java)
            .warn("Failed to parse document body as JSON: {}", e.message)
        emptyMap()
    }

    return DocumentResponse(
        id = id.toString(),
        userId = owner.id.toString(),
        documentType = documentType,
        body = bodyMap,
        validFrom = issuedAt,
        validUntil = expiresAt,
        attachToProfile = attachToProfile,
    )
}

fun DocumentRequest.toEntity(owner: User): Document {
    val bodyJson = com.fasterxml.jackson.module.kotlin.jacksonObjectMapper().writeValueAsString(body)

    return Document(
        documentType = documentType,
        body = bodyJson,
        issuedAt = validFrom ?: Instant.now(),
        expiresAt = validUntil,
        owner = owner,
        attachToProfile = attachToProfile,
    )
}

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
