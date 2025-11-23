package com.arekalov.papersplease.service

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.event.EventRequest
import com.arekalov.papersplease.dto.event.EventRequestPartial
import com.arekalov.papersplease.dto.event.EventResponse
import com.arekalov.papersplease.exception.ResourceNotFoundException
import com.arekalov.papersplease.mapper.toEntity
import com.arekalov.papersplease.mapper.toResponse
import com.arekalov.papersplease.repository.EventRepository
import com.arekalov.papersplease.repository.ShiftRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class EventService(
    private val eventRepository: EventRepository,
    private val shiftRepository: ShiftRepository,
) {

    @Transactional(readOnly = true)
    fun getAll(limit: Int, offset: Int): PagedResponse<EventResponse> {
        val pageable = PageRequest.of(offset / limit, limit)
        val page = eventRepository.findAll(pageable)

        return PagedResponse(
            items = page.content.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional(readOnly = true)
    fun getById(id: String): EventResponse {
        val event = eventRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Event with id $id not found") }
        return event.toResponse()
    }

    @Transactional(readOnly = true)
    fun getByShift(shiftId: String, limit: Int, offset: Int): PagedResponse<EventResponse> {
        val events = eventRepository.findByShift_Id(UUID.fromString(shiftId))
        val totalCount = events.size.toLong()

        val paginatedEvents = events
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
    fun create(request: EventRequest): EventResponse {
        val shift = shiftRepository.findById(UUID.fromString(request.shiftId))
            .orElseThrow { ResourceNotFoundException("Shift with id ${request.shiftId} not found") }

        val event = request.toEntity(shift)

        return eventRepository.save(event).toResponse()
    }

    @Transactional
    fun update(id: String, request: EventRequest): EventResponse {
        val event = eventRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Event with id $id not found") }

        val shift = shiftRepository.findById(UUID.fromString(request.shiftId))
            .orElseThrow { ResourceNotFoundException("Shift with id ${request.shiftId} not found") }

        event.apply {
            this.shift = shift
            description = request.description
        }

        return eventRepository.save(event).toResponse()
    }

    @Transactional
    fun partialUpdate(id: String, request: EventRequestPartial): EventResponse {
        val event = eventRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Event with id $id not found") }

        request.shiftId?.let { shiftId ->
            event.shift = shiftRepository.findById(UUID.fromString(shiftId))
                .orElseThrow { ResourceNotFoundException("Shift with id $shiftId not found") }
        }
        request.description?.let { event.description = it }

        return eventRepository.save(event).toResponse()
    }

    @Transactional
    fun delete(id: String) {
        val event = eventRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Event with id $id not found") }
        eventRepository.delete(event)
    }
}
