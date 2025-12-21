package com.arekalov.papersplease.service

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.shift.ShiftDetailedResponse
import com.arekalov.papersplease.dto.shift.ShiftFilterRequest
import com.arekalov.papersplease.dto.shift.ShiftRequest
import com.arekalov.papersplease.dto.shift.ShiftRequestPartial
import com.arekalov.papersplease.dto.shift.ShiftResponse
import com.arekalov.papersplease.exception.ConflictException
import com.arekalov.papersplease.exception.ForbiddenException
import com.arekalov.papersplease.exception.ResourceNotFoundException
import com.arekalov.papersplease.mapper.toResponse
import com.arekalov.papersplease.model.entity.Shift
import com.arekalov.papersplease.model.entity.Upk
import com.arekalov.papersplease.model.entity.User
import com.arekalov.papersplease.model.enums.Role
import com.arekalov.papersplease.repository.ParticipationRepository
import com.arekalov.papersplease.repository.ShiftRepository
import com.arekalov.papersplease.repository.TicketRepository
import com.arekalov.papersplease.repository.UpkRepository
import com.arekalov.papersplease.repository.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.UUID

@Service
class ShiftService(
    private val shiftRepository: ShiftRepository,
    private val upkRepository: UpkRepository,
    private val userRepository: UserRepository,
    private val participationRepository: ParticipationRepository,
    private val ticketRepository: TicketRepository,
) {

    @Transactional(readOnly = true)
    fun getAll(
        currentUserId: String,
        filters: ShiftFilterRequest,
        limit: Int,
        offset: Int,
    ): PagedResponse<ShiftResponse> {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val allShifts = getAccessibleShifts(currentUser)
        val filteredShifts = applyFilters(allShifts, filters)
        val paginatedShifts = filteredShifts.drop(offset).take(limit)

        return PagedResponse(
            items = paginatedShifts.map { it.toResponse() },
            total = filteredShifts.size.toLong(),
            limit = limit,
            offset = offset,
        )
    }

    private fun getAccessibleShifts(currentUser: User): List<Shift> {
        val userId = currentUser.id ?: throw IllegalStateException("User ID cannot be null")
        return when (currentUser.role) {
            Role.GOD -> shiftRepository.findAll()
            Role.BOSS -> {
                val bossUpkId = currentUser.upk?.id
                    ?: throw ForbiddenException("Boss must be assigned to UPK")
                shiftRepository.findByUpk_Id(bossUpkId)
            }
            Role.INSPECTOR, Role.SECURITY -> {
                shiftRepository.findByParticipations_User_Id(userId, Pageable.unpaged()).content
            }
            Role.MIGRANT -> throw ForbiddenException("Migrants do not have access to shifts")
        }
    }

    private fun applyFilters(shifts: List<Shift>, filters: ShiftFilterRequest): List<Shift> {
        return shifts.filter { shift ->
            matchesCreatedBy(shift, filters.createdBy) &&
                matchesUpk(shift, filters.upkId) &&
                matchesEndTime(shift, filters.endTimeNotNull)
        }
    }

    private fun matchesCreatedBy(shift: Shift, createdBy: String?): Boolean {
        return createdBy == null || shift.createdBy.id.toString() == createdBy
    }

    private fun matchesUpk(shift: Shift, upkId: String?): Boolean {
        return upkId == null || shift.upk.id.toString() == upkId
    }

    private fun matchesEndTime(shift: Shift, endTimeNotNull: Boolean?): Boolean {
        return endTimeNotNull == null || (endTimeNotNull == (shift.endTime != null))
    }

    @Transactional(readOnly = true)
    fun getById(currentUserId: String, id: String): ShiftResponse {
        val shift = shiftRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Shift with id $id not found") }

        checkAccessToShift(currentUserId, shift)

        return shift.toResponse()
    }

    @Transactional(readOnly = true)
    fun getDetailedById(currentUserId: String, id: String): ShiftDetailedResponse {
        val shift = shiftRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Shift with id $id not found") }

        checkAccessToShift(currentUserId, shift)

        val boss = userRepository.findByRoleAndUpk_Id(Role.BOSS, shift.upk.id!!)
            .firstOrNull() ?: throw ResourceNotFoundException("Boss not found for UPK ${shift.upk.id}")

        val participations = participationRepository.findByShift_Id(shift.id!!)

        val inspectors = participations
            .filter { it.user.role == Role.INSPECTOR }
            .map { participation ->
                val userId = participation.user.id!!
                val userName = participation.user.name

                val resolvedTickets = ticketRepository.findByExecutor_Id(
                    userId,
                    org.springframework.data.domain.Pageable.unpaged(),
                )
                    .content
                    .count { ticket ->
                        ticket.shift?.id == shift.id &&
                            (
                                ticket.status == com.arekalov.papersplease.model.enums.TicketStatus.CLOSED ||
                                    ticket.status == com.arekalov.papersplease.model.enums.TicketStatus.REJECTED ||
                                    ticket.status == com.arekalov.papersplease.model.enums.TicketStatus.APPROVED
                                )
                    }

                com.arekalov.papersplease.dto.shift.InspectorShiftInfo(
                    participationId = participation.id.toString(),
                    userId = userId.toString(),
                    name = userName,
                    shiftId = shift.id.toString(),
                    wage = participation.wage,
                    penalty = participation.penalty,
                    specialization = participation.specialization,
                    resolvedTickets = resolvedTickets,
                    passedCrossChecks = 0,
                )
            }

        return ShiftDetailedResponse(
            id = shift.id.toString(),
            startTime = shift.startTime,
            endTime = shift.endTime,
            createdBy = shift.createdBy.id.toString(),
            upk = shift.upk.toResponse(),
            boss = boss.toResponse(),
            inspectors = inspectors,
        )
    }

    @Transactional
    fun create(currentUserId: String, request: ShiftRequest): ShiftResponse {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val upk = upkRepository.findById(UUID.fromString(request.upkId))
            .orElseThrow { ResourceNotFoundException("UPK with id ${request.upkId} not found") }

        checkBossUpkAccess(currentUser, upk, "create shifts")

        val startTime = request.startTime ?: Instant.now()

        checkShiftForDateExists(startTime, upk.id!!)

        val shift = Shift(
            startTime = startTime,
            endTime = request.endTime,
            createdBy = currentUser,
            upk = upk,
        )

        return shiftRepository.save(shift).toResponse()
    }

    @Transactional
    fun partialUpdate(currentUserId: String, id: String, request: ShiftRequestPartial): ShiftResponse {
        val shift = shiftRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Shift with id $id not found") }

        checkAccessToShift(currentUserId, shift)

        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        request.startTime?.let { newStartTime ->
            if (newStartTime != shift.startTime) {
                checkShiftForDateExists(newStartTime, shift.upk.id!!, excludeShiftId = shift.id)
            }
            shift.startTime = newStartTime
        }

        request.endTime?.let { shift.endTime = it }
        request.upkId?.let { upkId ->
            val upk = upkRepository.findById(UUID.fromString(upkId))
                .orElseThrow { ResourceNotFoundException("UPK with id $upkId not found") }

            checkBossUpkAccess(currentUser, upk, "update shifts")

            if (upk.id != shift.upk.id) {
                checkShiftForDateExists(shift.startTime, upk.id!!, excludeShiftId = shift.id)
            }
            shift.upk = upk
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
            Role.GOD -> {
                return
            }
            Role.BOSS -> {
                val upkId = currentUser.upk?.id
                    ?: throw ForbiddenException("Boss must be assigned to UPK")

                if (shift.upk.id != upkId) {
                    throw ForbiddenException("Boss can only access shifts from their own UPK")
                }
            }
            Role.INSPECTOR, Role.SECURITY -> {
                val hasParticipation = shift.participations.any { it.user.id == currentUser.id }
                if (!hasParticipation) {
                    throw ForbiddenException("You can only access shifts you participated in")
                }
            }
            Role.MIGRANT -> {
                throw ForbiddenException("Migrants do not have access to shifts")
            }
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

    private fun checkShiftForDateExists(startTime: Instant, upkId: UUID, excludeShiftId: UUID? = null) {
        val date = LocalDate.ofInstant(startTime, ZoneOffset.UTC)

        val existingShifts = shiftRepository.findByUpk_Id(upkId).filter { shift ->
            val shiftDate = LocalDate.ofInstant(shift.startTime, ZoneOffset.UTC)
            shiftDate == date && (excludeShiftId == null || shift.id != excludeShiftId)
        }

        if (existingShifts.isNotEmpty()) {
            throw ConflictException(
                "A shift already exists for this UPK on $date. " +
                    "Only one shift per day is allowed.",
            )
        }
    }
}
