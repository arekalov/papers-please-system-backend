package com.arekalov.papersplease.service

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.event.EventRequest
import com.arekalov.papersplease.dto.event.EventRequestPartial
import com.arekalov.papersplease.dto.event.EventResponse
import com.arekalov.papersplease.exception.ForbiddenException
import com.arekalov.papersplease.exception.ResourceNotFoundException
import com.arekalov.papersplease.mapper.toEntity
import com.arekalov.papersplease.mapper.toResponse
import com.arekalov.papersplease.model.entity.Event
import com.arekalov.papersplease.model.entity.User
import com.arekalov.papersplease.model.enums.Role
import com.arekalov.papersplease.repository.EventRepository
import com.arekalov.papersplease.repository.ParticipationRepository
import com.arekalov.papersplease.repository.ShiftRepository
import com.arekalov.papersplease.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class EventService(
    private val eventRepository: EventRepository,
    private val shiftRepository: ShiftRepository,
    private val userRepository: UserRepository,
    private val participationRepository: ParticipationRepository,
) {

    @Transactional(readOnly = true)
    fun getAll(currentUserId: String, limit: Int, offset: Int): PagedResponse<EventResponse> {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val pageable = PageRequest.of(offset / limit, limit)
        val page = eventRepository.findAll(pageable)

        return PagedResponse(
            items = page.content.filter { checkReadAccess(currentUser, it) }.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional(readOnly = true)
    fun getById(currentUserId: String, id: String): EventResponse {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val event = eventRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Event with id $id not found") }

        if (!checkReadAccess(currentUser, event)) {
            throw ForbiddenException("You don't have access to this event")
        }

        return event.toResponse()
    }

    @Transactional(readOnly = true)
    fun getByShift(currentUserId: String, shiftId: String, limit: Int, offset: Int): PagedResponse<EventResponse> {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val events = eventRepository.findByShift_Id(UUID.fromString(shiftId))
        val totalCount = events.size.toLong()

        val paginatedEvents = events
            .filter { checkReadAccess(currentUser, it) }
            .drop(offset)
            .take(limit)

        return PagedResponse(
            items = paginatedEvents.map { it.toResponse() },
            total = totalCount,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional
    fun create(currentUserId: String, request: EventRequest): EventResponse {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        if (currentUser.role != Role.GOD) {
            throw ForbiddenException("Only gods can create events")
        }

        val shift = shiftRepository.findById(UUID.fromString(request.shiftId))
            .orElseThrow { ResourceNotFoundException("Shift with id ${request.shiftId} not found") }

        val event = request.toEntity(shift)

        return eventRepository.save(event).toResponse()
    }

    @Transactional
    fun update(currentUserId: String, id: String, request: EventRequest): EventResponse {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val event = eventRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Event with id $id not found") }

        checkUpdateAccess(currentUser, event)

        val shift = shiftRepository.findById(UUID.fromString(request.shiftId))
            .orElseThrow { ResourceNotFoundException("Shift with id ${request.shiftId} not found") }

        event.apply {
            this.shift = shift
            time = request.time
            description = request.description
            specialization = request.specialization
        }

        return eventRepository.save(event).toResponse()
    }

    @Transactional
    fun partialUpdate(currentUserId: String, id: String, request: EventRequestPartial): EventResponse {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val event = eventRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Event with id $id not found") }

        checkUpdateAccess(currentUser, event)

        request.shiftId?.let { shiftId ->
            event.shift = shiftRepository.findById(UUID.fromString(shiftId))
                .orElseThrow { ResourceNotFoundException("Shift with id $shiftId not found") }
        }
        request.time?.let { event.time = it }
        request.description?.let { event.description = it }
        request.specialization?.let { event.specialization = it }

        return eventRepository.save(event).toResponse()
    }

    @Transactional
    fun delete(currentUserId: String, id: String) {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        if (currentUser.role != Role.GOD) {
            throw ForbiddenException("Only gods can delete events")
        }

        val event = eventRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Event with id $id not found") }
        eventRepository.delete(event)
    }

    private fun checkReadAccess(currentUser: User, event: Event): Boolean {
        return when (currentUser.role) {
            Role.GOD -> true
            Role.MIGRANT -> false
            Role.BOSS -> {
                val upkId = currentUser.upk?.id ?: return false
                event.shift.upk.id == upkId
            }
            Role.INSPECTOR, Role.SECURITY -> {
                val participations = participationRepository.findByUser_Id(currentUser.id!!)
                val relevantParticipation = participations.find { it.shift.id == event.shift.id }
                    ?: return false
                if (event.specialization != null && event.specialization != relevantParticipation.specialization) {
                    return false
                }
                true
            }
        }
    }

    private fun checkUpdateAccess(currentUser: User, event: Event) {
        when (currentUser.role) {
            Role.GOD -> return
            Role.BOSS -> {
                val upkId = currentUser.upk?.id
                    ?: throw ForbiddenException("Boss must be assigned to UPK")
                if (event.shift.upk.id != upkId) {
                    throw ForbiddenException("You can only update events for shifts in your UPK")
                }
            }
            else -> throw ForbiddenException("You don't have permission to update events")
        }
    }
}
