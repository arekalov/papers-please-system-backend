package com.arekalov.papersplease.service

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.participation.ParticipationRequest
import com.arekalov.papersplease.dto.participation.ParticipationRequestPartial
import com.arekalov.papersplease.dto.participation.ParticipationResponse
import com.arekalov.papersplease.exception.ConflictException
import com.arekalov.papersplease.exception.ForbiddenException
import com.arekalov.papersplease.exception.ResourceNotFoundException
import com.arekalov.papersplease.mapper.toEntity
import com.arekalov.papersplease.mapper.toResponse
import com.arekalov.papersplease.model.entity.Participation
import com.arekalov.papersplease.model.entity.Upk
import com.arekalov.papersplease.model.enums.NotificationType
import com.arekalov.papersplease.model.enums.Role
import com.arekalov.papersplease.repository.ParticipationRepository
import com.arekalov.papersplease.repository.ShiftRepository
import com.arekalov.papersplease.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ParticipationService(
    private val participationRepository: ParticipationRepository,
    private val userRepository: UserRepository,
    private val shiftRepository: ShiftRepository,
    private val notificationService: NotificationService,
) {

    @Transactional(readOnly = true)
    fun getAll(currentUserId: String, limit: Int, offset: Int): PagedResponse<ParticipationResponse> {
        val pageable = PageRequest.of(offset / limit, limit)
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val page = when (currentUser.role) {
            Role.GOD -> {
                participationRepository.findAll(pageable)
            }
            Role.BOSS -> {
                val upkId = currentUser.upk?.id
                    ?: throw ForbiddenException("Boss must be assigned to UPK")
                participationRepository.findByShift_Upk_Id(upkId, pageable)
            }
            Role.INSPECTOR, Role.SECURITY -> {
                participationRepository.findByUser_Id(UUID.fromString(currentUserId), pageable)
            }
            Role.MIGRANT -> {
                throw ForbiddenException("Migrants do not have access to participations")
            }
        }

        return PagedResponse(
            items = page.content.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional(readOnly = true)
    fun getById(currentUserId: String, id: String): ParticipationResponse {
        val participation = participationRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Participation with id $id not found") }

        checkAccessToParticipation(currentUserId, participation)

        return participation.toResponse()
    }

    @Transactional(readOnly = true)
    fun getByShift(
        currentUserId: String,
        shiftId: String,
        limit: Int,
        offset: Int,
    ): PagedResponse<ParticipationResponse> {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val participations = participationRepository.findByShift_Id(UUID.fromString(shiftId))

        val filteredParticipations = when (currentUser.role) {
            Role.GOD -> participations
            Role.BOSS -> {
                val upkId = currentUser.upk?.id
                    ?: throw ForbiddenException("Boss must be assigned to UPK")
                participations.filter { it.shift.upk.id == upkId }
            }
            Role.INSPECTOR, Role.SECURITY -> {
                participations.filter { it.user.id == currentUser.id }
            }
            Role.MIGRANT -> {
                throw ForbiddenException("Migrants do not have access to participations")
            }
        }

        val totalCount = filteredParticipations.size.toLong()
        val paginatedParticipations = filteredParticipations
            .drop(offset)
            .take(limit)

        return PagedResponse(
            items = paginatedParticipations.map { it.toResponse() },
            total = totalCount,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional(readOnly = true)
    fun getByUser(
        currentUserId: String,
        userId: String,
        limit: Int,
        offset: Int,
    ): PagedResponse<ParticipationResponse> {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val participations = participationRepository.findByUser_Id(UUID.fromString(userId))

        val filteredParticipations = when (currentUser.role) {
            Role.GOD -> participations
            Role.BOSS -> {
                val upkId = currentUser.upk?.id
                    ?: throw ForbiddenException("Boss must be assigned to UPK")
                participations.filter { it.shift.upk.id == upkId }
            }
            Role.INSPECTOR, Role.SECURITY -> {
                if (currentUser.id.toString() != userId) {
                    throw ForbiddenException("Employees can only view their own participations")
                }
                participations
            }
            Role.MIGRANT -> {
                throw ForbiddenException("Migrants do not have access to participations")
            }
        }

        val totalCount = filteredParticipations.size.toLong()
        val paginatedParticipations = filteredParticipations
            .drop(offset)
            .take(limit)

        return PagedResponse(
            items = paginatedParticipations.map { it.toResponse() },
            total = totalCount,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional
    fun create(currentUserId: String, request: ParticipationRequest): ParticipationResponse {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val shift = shiftRepository.findById(UUID.fromString(request.shiftId))
            .orElseThrow { ResourceNotFoundException("Shift with id ${request.shiftId} not found") }

        checkAccessToUpk(currentUser, shift.upk)

        val user = userRepository.findById(UUID.fromString(request.userId))
            .orElseThrow { ResourceNotFoundException("User with id ${request.userId} not found") }

        checkUserNotInShift(shift.id!!, user.id!!)

        val participation = request.toEntity(shift, user)

        val savedParticipation = participationRepository.save(participation)

        notificationService.createSystemNotification(
            userId = user.id!!,
            type = NotificationType.SHIFT_STARTED,
            message = "You have been assigned to shift at ${shift.upk.name} (${shift.startTime} - ${shift.endTime})",
            shiftId = shift.id!!,
        )

        return savedParticipation.toResponse()
    }

    @Transactional
    fun partialUpdate(currentUserId: String, id: String, request: ParticipationRequestPartial): ParticipationResponse {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val participation = participationRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Participation with id $id not found") }

        checkAccessToUpk(currentUser, participation.shift.upk)

        request.shiftId?.let { shiftId ->
            val newShift = shiftRepository.findById(UUID.fromString(shiftId))
                .orElseThrow { ResourceNotFoundException("Shift with id $shiftId not found") }

            checkAccessToUpk(currentUser, newShift.upk)

            if (newShift.id != participation.shift.id) {
                checkUserNotInShift(newShift.id!!, participation.user.id!!, excludeParticipationId = participation.id)
            }
            participation.shift = newShift
        }
        request.userId?.let { userId ->
            val newUser = userRepository.findById(UUID.fromString(userId))
                .orElseThrow { ResourceNotFoundException("User with id $userId not found") }

            if (newUser.id != participation.user.id) {
                checkUserNotInShift(participation.shift.id!!, newUser.id!!, excludeParticipationId = participation.id)
            }
            participation.user = newUser
        }
        request.specialization?.let { participation.specialization = it }
        request.coeffBonus?.let { participation.bonusCoefficient = it }
        request.coeffPenalty?.let { participation.penaltyCoefficient = it }

        return participationRepository.save(participation).toResponse()
    }

    @Transactional
    fun delete(currentUserId: String, id: String) {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        val participation = participationRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Participation with id $id not found") }

        checkAccessToUpk(currentUser, participation.shift.upk)

        notificationService.deleteByShift(participation.shift.id!!)

        participationRepository.delete(participation)
    }

    private fun checkUserNotInShift(shiftId: UUID, userId: UUID, excludeParticipationId: UUID? = null) {
        val existingParticipations = participationRepository.findByShift_Id(shiftId)
            .filter { participation ->
                participation.user.id == userId &&
                    (excludeParticipationId == null || participation.id != excludeParticipationId)
            }

        if (existingParticipations.isNotEmpty()) {
            throw ConflictException(
                "User is already participating in this shift. " +
                    "Only one participation per user per shift is allowed.",
            )
        }
    }

    private fun checkAccessToParticipation(currentUserId: String, participation: Participation) {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        when (currentUser.role) {
            Role.GOD -> return
            Role.BOSS -> {
                val upkId = currentUser.upk?.id
                    ?: throw ForbiddenException("Boss must be assigned to UPK")
                if (participation.shift.upk.id != upkId) {
                    throw ForbiddenException("You don't have access to this participation")
                }
            }
            Role.INSPECTOR, Role.SECURITY -> {
                if (participation.user.id != currentUser.id) {
                    throw ForbiddenException("Employees can only view their own participations")
                }
            }
            Role.MIGRANT -> {
                throw ForbiddenException("Migrants do not have access to participations")
            }
        }
    }

    private fun checkAccessToUpk(currentUser: com.arekalov.papersplease.model.entity.User, upk: Upk) {
        when (currentUser.role) {
            Role.GOD -> return
            Role.BOSS -> {
                val userUpkId = currentUser.upk?.id
                    ?: throw ForbiddenException("Boss must be assigned to UPK")
                if (upk.id != userUpkId) {
                    throw ForbiddenException("You can only manage participations for your UPK")
                }
            }
            else -> throw ForbiddenException("You don't have permission to perform this action")
        }
    }
}
