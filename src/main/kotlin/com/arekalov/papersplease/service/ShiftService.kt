package com.arekalov.papersplease.service

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.shift.ShiftRequest
import com.arekalov.papersplease.dto.shift.ShiftRequestPartial
import com.arekalov.papersplease.dto.shift.ShiftResponse
import com.arekalov.papersplease.exception.ForbiddenException
import com.arekalov.papersplease.exception.ResourceNotFoundException
import com.arekalov.papersplease.mapper.toEntity
import com.arekalov.papersplease.mapper.toResponse
import com.arekalov.papersplease.model.entity.Upk
import com.arekalov.papersplease.model.entity.User
import com.arekalov.papersplease.model.enums.Role
import com.arekalov.papersplease.repository.ShiftRepository
import com.arekalov.papersplease.repository.UpkRepository
import com.arekalov.papersplease.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ShiftService(
    private val shiftRepository: ShiftRepository,
    private val upkRepository: UpkRepository,
    private val userRepository: UserRepository,
) {

    @Transactional(readOnly = true)
    fun getAll(currentUserId: String, limit: Int, offset: Int): PagedResponse<ShiftResponse> {
        val pageable = PageRequest.of(offset / limit, limit)
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val page = currentUser.id?.let { userId ->
            when (currentUser.role) {
                Role.BOSS -> shiftRepository.findByCreatedBy_Id(userId, pageable)
                Role.INSPECTOR, Role.MIGRANT -> shiftRepository.findByParticipations_User_Id(userId, pageable)
                else -> shiftRepository.findAll(pageable)
            }
        } ?: throw IllegalStateException("User ID cannot be null for persisted entity")

        return PagedResponse(
            items = page.content.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional(readOnly = true)
    fun getById(currentUserId: String, id: String): ShiftResponse {
        val shift = shiftRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Shift with id $id not found") }

        checkAccessToShift(currentUserId, shift)

        return shift.toResponse()
    }

    @Transactional
    fun create(currentUserId: String, request: ShiftRequest): ShiftResponse {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val upk = upkRepository.findById(UUID.fromString(request.upkId))
            .orElseThrow { ResourceNotFoundException("UPK with id ${request.upkId} not found") }

        checkBossUpkAccess(currentUser, upk, "create shifts")

        val createdBy = userRepository.findById(UUID.fromString(request.createdBy))
            .orElseThrow { ResourceNotFoundException("User with id ${request.createdBy} not found") }

        val shift = request.toEntity(upk, createdBy)

        return shiftRepository.save(shift).toResponse()
    }

    @Transactional
    fun partialUpdate(currentUserId: String, id: String, request: ShiftRequestPartial): ShiftResponse {
        val shift = shiftRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Shift with id $id not found") }

        checkAccessToShift(currentUserId, shift)

        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        request.shiftDate?.let { shift.shiftDate = it }
        request.upkId?.let { upkId ->
            val upk = upkRepository.findById(UUID.fromString(upkId))
                .orElseThrow { ResourceNotFoundException("UPK with id $upkId not found") }

            checkBossUpkAccess(currentUser, upk, "update shifts")
            shift.upk = upk
        }
        request.createdBy?.let { createdById ->
            shift.createdBy = userRepository.findById(UUID.fromString(createdById))
                .orElseThrow { ResourceNotFoundException("User with id $createdById not found") }
        }

        return shiftRepository.save(shift).toResponse()
    }

    @Transactional
    fun delete(currentUserId: String, id: String) {
        val shift = shiftRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Shift with id $id not found") }

        checkAccessToShift(currentUserId, shift)

        shiftRepository.delete(shift)
    }

    private fun checkAccessToShift(currentUserId: String, shift: com.arekalov.papersplease.model.entity.Shift) {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        when (currentUser.role) {
            Role.BOSS -> {
                currentUser.upk?.id?.let { currentUserUpkId ->
                    if (shift.upk.id != currentUserUpkId) {
                        throw ForbiddenException("Boss can only access shifts from their own UPK")
                    }
                } ?: throw ForbiddenException("Boss must be assigned to UPK")
            }
            Role.INSPECTOR, Role.MIGRANT -> {
                throw ForbiddenException("Employees can only view shifts through the list endpoint")
            }
            else -> {}
        }
    }

    private fun checkBossUpkAccess(currentUser: User, upk: Upk, action: String) {
        if (currentUser.role == Role.BOSS) {
            val currentUserUpkId = currentUser.upk?.id
                ?: throw ForbiddenException("Boss must be assigned to UPK")

            if (upk.id != currentUserUpkId) {
                throw ForbiddenException("Boss can only $action for their own UPK")
            }
        }
    }
}
