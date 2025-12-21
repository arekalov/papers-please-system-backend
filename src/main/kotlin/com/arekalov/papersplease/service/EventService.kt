package com.arekalov.papersplease.service

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.event.EventRequest
import com.arekalov.papersplease.dto.event.EventRequestPartial
import com.arekalov.papersplease.dto.event.EventResponse
import com.arekalov.papersplease.exception.ForbiddenException
import com.arekalov.papersplease.exception.ResourceNotFoundException
import com.arekalov.papersplease.mapper.toResponse
import com.arekalov.papersplease.model.entity.Event
import com.arekalov.papersplease.model.entity.User
import com.arekalov.papersplease.model.enums.Role
import com.arekalov.papersplease.repository.EventRepository
import com.arekalov.papersplease.repository.UpkRepository
import com.arekalov.papersplease.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class EventService(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository,
    private val upkRepository: UpkRepository,
) {

    @Transactional(readOnly = true)
    fun getAll(currentUserId: String, limit: Int, offset: Int, upkId: String? = null): PagedResponse<EventResponse> {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val pageable = PageRequest.of(offset / limit, limit)
        val page = eventRepository.findAll(pageable)

        val filteredEvents = page.content.filter { event ->
            checkReadAccess(currentUser) &&
                checkUpkAccess(currentUser, event) &&
                matchesUpkFilter(event, upkId)
        }

        return PagedResponse(
            items = filteredEvents.map { it.toResponse() },
            total = filteredEvents.size.toLong(),
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

        if (!checkReadAccess(currentUser) || !checkUpkAccess(currentUser, event)) {
            throw ForbiddenException("You don't have access to this event")
        }

        return event.toResponse()
    }

    @Transactional
    fun create(currentUserId: String, request: EventRequest): EventResponse {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        checkCreateAccess(currentUser, request.upkId)

        val upk = upkRepository.findById(UUID.fromString(request.upkId))
            .orElseThrow { ResourceNotFoundException("UPK with id ${request.upkId} not found") }

        val event = Event(
            time = request.time,
            description = request.description,
            specialization = request.specialization,
            priority = request.priority,
            upk = upk,
        )

        return eventRepository.save(event).toResponse()
    }

    @Transactional
    fun update(currentUserId: String, id: String, request: EventRequest): EventResponse {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val event = eventRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Event with id $id not found") }

        checkUpdateAccess(currentUser, event)

        val upk = upkRepository.findById(UUID.fromString(request.upkId))
            .orElseThrow { ResourceNotFoundException("UPK with id ${request.upkId} not found") }

        event.apply {
            time = request.time
            description = request.description
            specialization = request.specialization
            priority = request.priority
            this.upk = upk
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

        request.time?.let { event.time = it }
        request.description?.let { event.description = it }
        request.specialization?.let { event.specialization = it }
        request.priority?.let { event.priority = it }
        request.upkId?.let { upkId ->
            val upk = upkRepository.findById(UUID.fromString(upkId))
                .orElseThrow { ResourceNotFoundException("UPK with id $upkId not found") }
            event.upk = upk
        }

        return eventRepository.save(event).toResponse()
    }

    @Transactional
    fun delete(currentUserId: String, id: String) {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val event = eventRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Event with id $id not found") }

        when (currentUser.role) {
            Role.GOD -> {}
            Role.BOSS -> {
                val userUpkId = currentUser.upk?.id
                    ?: throw ForbiddenException("Boss must be assigned to UPK")
                if (event.upk.id != userUpkId) {
                    throw ForbiddenException("Boss can only delete events for their own UPK")
                }
            }
            else -> throw ForbiddenException("Only GOD and BOSS can delete events")
        }

        eventRepository.delete(event)
    }

    private fun checkReadAccess(currentUser: User): Boolean {
        return when (currentUser.role) {
            Role.GOD, Role.BOSS, Role.INSPECTOR, Role.SECURITY -> true
            Role.MIGRANT -> false
        }
    }

    private fun checkUpkAccess(currentUser: User, event: com.arekalov.papersplease.model.entity.Event): Boolean {
        return when (currentUser.role) {
            Role.GOD -> true
            Role.BOSS, Role.INSPECTOR, Role.SECURITY -> {
                val userUpkId = currentUser.upk?.id ?: return false
                event.upk.id == userUpkId
            }
            Role.MIGRANT -> false
        }
    }

    private fun checkCreateAccess(currentUser: User, upkId: String) {
        when (currentUser.role) {
            Role.GOD -> return
            Role.BOSS -> {
                val userUpkId = currentUser.upk?.id?.toString()
                    ?: throw ForbiddenException("Boss must be assigned to UPK")
                if (userUpkId != upkId) {
                    throw ForbiddenException("Boss can only create events for their own UPK")
                }
            }
            else -> throw ForbiddenException("Only GOD and BOSS can create events")
        }
    }

    private fun checkUpdateAccess(currentUser: User, event: com.arekalov.papersplease.model.entity.Event) {
        when (currentUser.role) {
            Role.GOD -> return
            Role.BOSS -> {
                val userUpkId = currentUser.upk?.id
                    ?: throw ForbiddenException("Boss must be assigned to UPK")
                if (event.upk.id != userUpkId) {
                    throw ForbiddenException("Boss can only update events for their own UPK")
                }
            }
            else -> throw ForbiddenException("Only GOD and BOSS can update events")
        }
    }

    private fun matchesUpkFilter(event: com.arekalov.papersplease.model.entity.Event, upkId: String?): Boolean {
        return upkId == null || event.upk.id.toString() == upkId
    }
}
