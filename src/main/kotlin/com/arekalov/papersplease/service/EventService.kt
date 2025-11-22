package com.arekalov.papersplease.service

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.event.EventRequest
import com.arekalov.papersplease.dto.event.EventRequestPartial
import com.arekalov.papersplease.dto.event.EventResponse
import com.arekalov.papersplease.exception.ResourceNotFoundException
import com.arekalov.papersplease.mapper.toEntity
import com.arekalov.papersplease.mapper.toResponse
import com.arekalov.papersplease.model.enums.Priority
import com.arekalov.papersplease.repository.EventRepository
import com.arekalov.papersplease.repository.ShiftRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    suspend fun getAll(limit: Int, offset: Int): PagedResponse<EventResponse> = withContext(Dispatchers.IO) {
        val pageable = PageRequest.of(offset / limit, limit)
        val page = eventRepository.findAll(pageable)

        PagedResponse(
            items = page.content.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional(readOnly = true)
    suspend fun getById(id: String): EventResponse = withContext(Dispatchers.IO) {
        val event = eventRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Event with id $id not found") }
        event.toResponse()
    }

    @Transactional(readOnly = true)
    suspend fun getByShift(shiftId: String, limit: Int, offset: Int): PagedResponse<EventResponse> =
        withContext(Dispatchers.IO) {
            val events = eventRepository.findByShift_Id(UUID.fromString(shiftId))
            val totalCount = events.size.toLong()

            val paginatedEvents = events
                .drop(offset)
                .take(limit)

            PagedResponse(
                items = paginatedEvents.map { it.toResponse() },
                total = totalCount,
                limit = limit,
                offset = offset,
            )
        }

    @Transactional(readOnly = true)
    suspend fun getByPriority(priority: Priority, limit: Int, offset: Int): PagedResponse<EventResponse> =
        withContext(Dispatchers.IO) {
            val events = eventRepository.findByPriority(priority)
            val totalCount = events.size.toLong()

            val paginatedEvents = events
                .drop(offset)
                .take(limit)

            PagedResponse(
                items = paginatedEvents.map { it.toResponse() },
                total = totalCount,
                limit = limit,
                offset = offset,
            )
        }

    @Transactional
    suspend fun create(request: EventRequest): EventResponse = withContext(Dispatchers.IO) {
        val shift = shiftRepository.findById(UUID.fromString(request.shiftId))
            .orElseThrow { ResourceNotFoundException("Shift with id ${request.shiftId} not found") }

        val event = request.toEntity(shift)

        eventRepository.save(event).toResponse()
    }

    @Transactional
    suspend fun update(id: String, request: EventRequest): EventResponse = withContext(Dispatchers.IO) {
        val event = eventRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Event with id $id not found") }

        val shift = shiftRepository.findById(UUID.fromString(request.shiftId))
            .orElseThrow { ResourceNotFoundException("Shift with id ${request.shiftId} not found") }

        event.apply {
            this.shift = shift
            description = request.description
        }

        eventRepository.save(event).toResponse()
    }

    @Transactional
    suspend fun partialUpdate(id: String, request: EventRequestPartial): EventResponse = withContext(Dispatchers.IO) {
        val event = eventRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Event with id $id not found") }

        request.shiftId?.let { shiftId ->
            event.shift = shiftRepository.findById(UUID.fromString(shiftId))
                .orElseThrow { ResourceNotFoundException("Shift with id $shiftId not found") }
        }
        request.description?.let { event.description = it }

        eventRepository.save(event).toResponse()
    }

    @Transactional
    suspend fun delete(id: String) = withContext(Dispatchers.IO) {
        val event = eventRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Event with id $id not found") }
        eventRepository.delete(event)
    }
}
