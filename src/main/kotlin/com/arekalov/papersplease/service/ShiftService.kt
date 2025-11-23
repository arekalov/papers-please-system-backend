package com.arekalov.papersplease.service

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.shift.ShiftRequest
import com.arekalov.papersplease.dto.shift.ShiftRequestPartial
import com.arekalov.papersplease.dto.shift.ShiftResponse
import com.arekalov.papersplease.exception.ResourceNotFoundException
import com.arekalov.papersplease.mapper.toEntity
import com.arekalov.papersplease.mapper.toResponse
import com.arekalov.papersplease.repository.ShiftRepository
import com.arekalov.papersplease.repository.UpkRepository
import com.arekalov.papersplease.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class ShiftService(
    private val shiftRepository: ShiftRepository,
    private val upkRepository: UpkRepository,
    private val userRepository: UserRepository,
) {

    @Transactional(readOnly = true)
    fun getAll(limit: Int, offset: Int): PagedResponse<ShiftResponse> {
        val pageable = PageRequest.of(offset / limit, limit)
        val page = shiftRepository.findAll(pageable)

        return PagedResponse(
            items = page.content.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional(readOnly = true)
    fun getById(id: String): ShiftResponse {
        val shift = shiftRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Shift with id $id not found") }
        return shift.toResponse()
    }

    @Transactional(readOnly = true)
    fun getByUpk(upkId: String, limit: Int, offset: Int): PagedResponse<ShiftResponse> {
        val shifts = shiftRepository.findByUpk_Id(UUID.fromString(upkId))
        val totalCount = shifts.size.toLong()

        val paginatedShifts = shifts
            .drop(offset)
            .take(limit)

        return PagedResponse(
            items = paginatedShifts.map { it.toResponse() },
            total = totalCount,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional(readOnly = true)
    fun getByDate(
        upkId: String,
        date: Instant,
    ): ShiftResponse? {
        val shift = shiftRepository.findByShiftDateAndUpk_Id(date, UUID.fromString(upkId))
        return shift?.toResponse()
    }

    @Transactional
    fun create(request: ShiftRequest): ShiftResponse {
        val upk = upkRepository.findById(UUID.fromString(request.upkId))
            .orElseThrow { ResourceNotFoundException("UPK with id ${request.upkId} not found") }

        val createdBy = userRepository.findById(UUID.fromString(request.createdBy))
            .orElseThrow { ResourceNotFoundException("User with id ${request.createdBy} not found") }

        val shift = request.toEntity(upk, createdBy)

        return shiftRepository.save(shift).toResponse()
    }

    @Transactional
    fun update(id: String, request: ShiftRequest): ShiftResponse {
        val shift = shiftRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Shift with id $id not found") }

        val upk = upkRepository.findById(UUID.fromString(request.upkId))
            .orElseThrow { ResourceNotFoundException("UPK with id ${request.upkId} not found") }

        val createdBy = userRepository.findById(UUID.fromString(request.createdBy))
            .orElseThrow { ResourceNotFoundException("User with id ${request.createdBy} not found") }

        shift.apply {
            shiftDate = request.shiftDate
            this.upk = upk
            this.createdBy = createdBy
        }

        return shiftRepository.save(shift).toResponse()
    }

    @Transactional
    fun partialUpdate(id: String, request: ShiftRequestPartial): ShiftResponse {
        val shift = shiftRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Shift with id $id not found") }

        request.shiftDate?.let { shift.shiftDate = it }
        request.upkId?.let { upkId ->
            shift.upk = upkRepository.findById(UUID.fromString(upkId))
                .orElseThrow { ResourceNotFoundException("UPK with id $upkId not found") }
        }
        request.createdBy?.let { createdById ->
            shift.createdBy = userRepository.findById(UUID.fromString(createdById))
                .orElseThrow { ResourceNotFoundException("User with id $createdById not found") }
        }

        return shiftRepository.save(shift).toResponse()
    }

    @Transactional
    fun delete(id: String) {
        val shift = shiftRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Shift with id $id not found") }
        shiftRepository.delete(shift)
    }
}
