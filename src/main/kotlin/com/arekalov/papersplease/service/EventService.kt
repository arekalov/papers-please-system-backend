package com.arekalov.papersplease.service

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.event.EventRequest
import com.arekalov.papersplease.dto.event.EventRequestPartial
import com.arekalov.papersplease.dto.event.EventResponse
import com.arekalov.papersplease.exception.ForbiddenException
import com.arekalov.papersplease.exception.ResourceNotFoundException
import com.arekalov.papersplease.mapper.toEntity
import com.arekalov.papersplease.mapper.toResponse
import com.arekalov.papersplease.model.entity.User
import com.arekalov.papersplease.model.enums.Role
import com.arekalov.papersplease.repository.EventRepository
import com.arekalov.papersplease.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class EventService(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository,
) {

    @Transactional(readOnly = true)
    fun getAll(currentUserId: String, limit: Int, offset: Int): PagedResponse<EventResponse> {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val pageable = PageRequest.of(offset / limit, limit)
        val page = eventRepository.findAll(pageable)

        return PagedResponse(
            items = page.content.filter { checkReadAccess(currentUser) }.map { it.toResponse() },
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

        if (!checkReadAccess(currentUser)) {
            throw ForbiddenException("You don't have access to this event")
        }

        return event.toResponse()
    }

    @Transactional
    fun create(currentUserId: String, request: EventRequest): EventResponse {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        if (currentUser.role != Role.GOD) {
            throw ForbiddenException("Only gods can create events")
        }

        val event = request.toEntity()

        return eventRepository.save(event).toResponse()
    }

    @Transactional
    fun update(currentUserId: String, id: String, request: EventRequest): EventResponse {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val event = eventRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Event with id $id not found") }

        checkUpdateAccess(currentUser)

        event.apply {
            time = request.time
            description = request.description
            specialization = request.specialization
            priority = request.priority
        }

        return eventRepository.save(event).toResponse()
    }

    @Transactional
    fun partialUpdate(currentUserId: String, id: String, request: EventRequestPartial): EventResponse {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val event = eventRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Event with id $id not found") }

        checkUpdateAccess(currentUser)

        request.time?.let { event.time = it }
        request.description?.let { event.description = it }
        request.specialization?.let { event.specialization = it }
        request.priority?.let { event.priority = it }

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

    private fun checkReadAccess(currentUser: User): Boolean {
        return when (currentUser.role) {
            Role.GOD, Role.BOSS, Role.INSPECTOR, Role.SECURITY -> true
            Role.MIGRANT -> false
        }
    }

    private fun checkUpdateAccess(currentUser: User) {
        if (currentUser.role != Role.GOD) {
            throw ForbiddenException("Only GOD can update events")
        }
    }
}
