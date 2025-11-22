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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    suspend fun getAll(limit: Int, offset: Int): PagedResponse<ParticipationResponse> = withContext(Dispatchers.IO) {
        val pageable = PageRequest.of(offset / limit, limit)
        val page = participationRepository.findAll(pageable)

        PagedResponse(
            items = page.content.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional(readOnly = true)
    suspend fun getById(id: String): ParticipationResponse = withContext(Dispatchers.IO) {
        val participation = participationRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Participation with id $id not found") }
        participation.toResponse()
    }

    @Transactional(readOnly = true)
    suspend fun getByShift(shiftId: String, limit: Int, offset: Int): PagedResponse<ParticipationResponse> =
        withContext(Dispatchers.IO) {
            val participations = participationRepository.findByShift_Id(UUID.fromString(shiftId))
            val totalCount = participations.size.toLong()

            val paginatedParticipations = participations
                .drop(offset)
                .take(limit)

            PagedResponse(
                items = paginatedParticipations.map { it.toResponse() },
                total = totalCount,
                limit = limit,
                offset = offset,
            )
        }

    @Transactional(readOnly = true)
    suspend fun getByUser(userId: String, limit: Int, offset: Int): PagedResponse<ParticipationResponse> =
        withContext(Dispatchers.IO) {
            val participations = participationRepository.findByUser_Id(UUID.fromString(userId))
            val totalCount = participations.size.toLong()

            val paginatedParticipations = participations
                .drop(offset)
                .take(limit)

            PagedResponse(
                items = paginatedParticipations.map { it.toResponse() },
                total = totalCount,
                limit = limit,
                offset = offset,
            )
        }

    @Transactional(readOnly = true)
    suspend fun getBySpecialization(
        specialization: Specialization,
        limit: Int,
        offset: Int,
    ): PagedResponse<ParticipationResponse> = withContext(Dispatchers.IO) {
        val participations = participationRepository.findBySpecialization(specialization)
        val totalCount = participations.size.toLong()

        val paginatedParticipations = participations
            .drop(offset)
            .take(limit)

        PagedResponse(
            items = paginatedParticipations.map { it.toResponse() },
            total = totalCount,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional
    suspend fun create(request: ParticipationRequest): ParticipationResponse = withContext(Dispatchers.IO) {
        val shift = shiftRepository.findById(UUID.fromString(request.shiftId))
            .orElseThrow { ResourceNotFoundException("Shift with id ${request.shiftId} not found") }

        val user = userRepository.findById(UUID.fromString(request.userId))
            .orElseThrow { ResourceNotFoundException("User with id ${request.userId} not found") }

        val participation = request.toEntity(shift, user)

        participationRepository.save(participation).toResponse()
    }

    @Transactional
    suspend fun update(id: String, request: ParticipationRequest): ParticipationResponse =
        withContext(Dispatchers.IO) {
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

            participationRepository.save(participation).toResponse()
        }

    @Transactional
    suspend fun partialUpdate(id: String, request: ParticipationRequestPartial): ParticipationResponse =
        withContext(Dispatchers.IO) {
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

            participationRepository.save(participation).toResponse()
        }

    @Transactional
    suspend fun delete(id: String) = withContext(Dispatchers.IO) {
        val participation = participationRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("Participation with id $id not found") }
        participationRepository.delete(participation)
    }
}
