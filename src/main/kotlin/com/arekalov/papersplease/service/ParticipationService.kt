package com.arekalov.papersplease.service

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.participation.ParticipationRequest
import com.arekalov.papersplease.dto.participation.ParticipationRequestPartial
import com.arekalov.papersplease.dto.participation.ParticipationResponse
import com.arekalov.papersplease.exception.ResourceNotFoundException
import com.arekalov.papersplease.mapper.toEntity
import com.arekalov.papersplease.mapper.toResponse
import com.arekalov.papersplease.model.enums.Specialization
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
    private val shiftRepository: ShiftRepository,
    private val userRepository: UserRepository,
) {

    @Transactional(readOnly = true)
    fun getAll(limit: Int, offset: Int): PagedResponse<ParticipationResponse> {
        val pageable = PageRequest.of(offset / limit, limit)
        val page = participationRepository.findAll(pageable)

        return PagedResponse(
            items = page.content.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional(readOnly = true)
    fun getById(id: String): ParticipationResponse {
        val participation = participationRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Participation with id $id not found") }
        return participation.toResponse()
    }

    @Transactional(readOnly = true)
    fun getByShift(shiftId: String, limit: Int, offset: Int): PagedResponse<ParticipationResponse> {
        val participations = participationRepository.findByShift_Id(UUID.fromString(shiftId))
        val totalCount = participations.size.toLong()

        val paginatedParticipations = participations
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
    fun getByUser(userId: String, limit: Int, offset: Int): PagedResponse<ParticipationResponse> {
        val participations = participationRepository.findByUser_Id(UUID.fromString(userId))
        val totalCount = participations.size.toLong()

        val paginatedParticipations = participations
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
    fun getBySpecialization(
        specialization: Specialization,
        limit: Int,
        offset: Int,
    ): PagedResponse<ParticipationResponse> {
        val participations = participationRepository.findBySpecialization(specialization)
        val totalCount = participations.size.toLong()

        val paginatedParticipations = participations
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
    fun create(request: ParticipationRequest): ParticipationResponse {
        val shift = shiftRepository.findById(UUID.fromString(request.shiftId))
            .orElseThrow { ResourceNotFoundException("Shift with id ${request.shiftId} not found") }

        val user = userRepository.findById(UUID.fromString(request.userId))
            .orElseThrow { ResourceNotFoundException("User with id ${request.userId} not found") }

        val participation = request.toEntity(shift, user)

        return participationRepository.save(participation).toResponse()
    }

    @Transactional
    fun update(id: String, request: ParticipationRequest): ParticipationResponse {
        val participation = participationRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Participation with id $id not found") }

        val shift = shiftRepository.findById(UUID.fromString(request.shiftId))
            .orElseThrow { ResourceNotFoundException("Shift with id ${request.shiftId} not found") }

        val user = userRepository.findById(UUID.fromString(request.userId))
            .orElseThrow { ResourceNotFoundException("User with id ${request.userId} not found") }

        participation.apply {
            this.shift = shift
            this.user = user
            specialization = request.specialization
            bonusCoefficient = request.coeffBonus
            penaltyCoefficient = request.coeffPenalty
        }

        return participationRepository.save(participation).toResponse()
    }

    @Transactional
    fun partialUpdate(id: String, request: ParticipationRequestPartial): ParticipationResponse {
        val participation = participationRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Participation with id $id not found") }

        request.shiftId?.let { shiftId ->
            participation.shift = shiftRepository.findById(UUID.fromString(shiftId))
                .orElseThrow { ResourceNotFoundException("Shift with id $shiftId not found") }
        }
        request.userId?.let { userId ->
            participation.user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow { ResourceNotFoundException("User with id $userId not found") }
        }
        request.specialization?.let { participation.specialization = it }
        request.coeffBonus?.let { participation.bonusCoefficient = it }
        request.coeffPenalty?.let { participation.penaltyCoefficient = it }

        return participationRepository.save(participation).toResponse()
    }

    @Transactional
    fun delete(id: String) {
        val participation = participationRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Participation with id $id not found") }
        participationRepository.delete(participation)
    }
}
