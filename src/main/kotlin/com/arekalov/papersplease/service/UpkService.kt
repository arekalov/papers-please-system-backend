package com.arekalov.papersplease.service

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.upk.UpkRequest
import com.arekalov.papersplease.dto.upk.UpkRequestPartial
import com.arekalov.papersplease.dto.upk.UpkResponse
import com.arekalov.papersplease.exception.ConflictException
import com.arekalov.papersplease.exception.ResourceNotFoundException
import com.arekalov.papersplease.mapper.toResponse
import com.arekalov.papersplease.model.entity.Upk
import com.arekalov.papersplease.repository.UpkRepository
import com.arekalov.papersplease.repository.UserRepository
import jakarta.persistence.EntityManager
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UpkService(
    private val upkRepository: UpkRepository,
    private val userRepository: UserRepository,
    private val entityManager: EntityManager,
) {

    @Transactional(readOnly = true)
    fun getAll(limit: Int, offset: Int): PagedResponse<UpkResponse> {
        val pageable = PageRequest.of(offset / limit, limit)
        val page = upkRepository.findAll(pageable)

        return PagedResponse(
            items = page.content.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional(readOnly = true)
    fun getById(id: String): UpkResponse {
        val upk = upkRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("UPK with id $id not found") }
        return upk.toResponse()
    }

    @Transactional
    fun create(request: UpkRequest): UpkResponse {
        val boss = request.bossId?.let {
            userRepository.findById(UUID.fromString(it))
                .orElseThrow { ResourceNotFoundException("User with id $it not found") }
        } ?: throw ResourceNotFoundException("Boss is required")

        val existingUpk = upkRepository.findByBossId(boss.id!!)
        if (existingUpk != null) {
            throw ConflictException("Boss is already assigned to UPK '${existingUpk.name}'")
        }

        val upk = Upk(
            name = request.name,
            region = request.region,
            boss = boss,
        )

        return upkRepository.save(upk).toResponse()
    }

    @Transactional
    fun partialUpdate(id: String, request: UpkRequestPartial): UpkResponse {
        val upk = upkRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("UPK with id $id not found") }

        request.name?.let { upk.name = it }
        request.region?.let { upk.region = it }
        request.bossId?.let { bossId ->
            val newBoss = userRepository.findById(UUID.fromString(bossId))
                .orElseThrow { ResourceNotFoundException("User with id $bossId not found") }

            val existingUpk = upkRepository.findByBossId(newBoss.id!!)
            if (existingUpk != null && existingUpk.id != upk.id) {
                throw ConflictException("Boss is already assigned to UPK '${existingUpk.name}'")
            }

            upk.boss = newBoss
        }

        return upkRepository.save(upk).toResponse()
    }

    @Transactional
    fun delete(id: String) {
        val upkId = UUID.fromString(id)

        if (!upkRepository.existsById(upkId)) {
            throw ResourceNotFoundException("UPK with id $id not found")
        }

        entityManager.createNativeQuery("DELETE FROM users WHERE upk_id = :upkId")
            .setParameter("upkId", upkId)
            .executeUpdate()

        entityManager.createNativeQuery("DELETE FROM upks WHERE id = :upkId")
            .setParameter("upkId", upkId)
            .executeUpdate()
    }
}
