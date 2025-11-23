package com.arekalov.papersplease.service

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.upk.UpkRequest
import com.arekalov.papersplease.dto.upk.UpkRequestPartial
import com.arekalov.papersplease.dto.upk.UpkResponse
import com.arekalov.papersplease.exception.ResourceNotFoundException
import com.arekalov.papersplease.mapper.toResponse
import com.arekalov.papersplease.model.entity.Upk
import com.arekalov.papersplease.model.enums.Region
import com.arekalov.papersplease.repository.UpkRepository
import com.arekalov.papersplease.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UpkService(
    private val upkRepository: UpkRepository,
    private val userRepository: UserRepository,
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

    @Transactional(readOnly = true)
    fun getByRegion(region: Region, limit: Int, offset: Int): PagedResponse<UpkResponse> {
        val upks = upkRepository.findByRegion(region)
        val totalCount = upks.size.toLong()

        val paginatedUpks = upks
            .drop(offset)
            .take(limit)

        return PagedResponse(
            items = paginatedUpks.map { it.toResponse() },
            total = totalCount,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional
    fun create(request: UpkRequest): UpkResponse {
        val boss = request.bossId?.let {
            userRepository.findById(UUID.fromString(it))
                .orElseThrow { ResourceNotFoundException("User with id $it not found") }
        } ?: throw ResourceNotFoundException("Boss is required")

        val upk = Upk(
            name = request.name,
            region = request.region,
            boss = boss,
        )

        return upkRepository.save(upk).toResponse()
    }

    @Transactional
    fun update(id: String, request: UpkRequest): UpkResponse {
        val upk = upkRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("UPK with id $id not found") }

        val boss = request.bossId?.let {
            userRepository.findById(UUID.fromString(it))
                .orElseThrow { ResourceNotFoundException("User with id $it not found") }
        } ?: throw ResourceNotFoundException("Boss is required")

        upk.apply {
            name = request.name
            region = request.region
            this.boss = boss
        }

        return upkRepository.save(upk).toResponse()
    }

    @Transactional
    fun partialUpdate(id: String, request: UpkRequestPartial): UpkResponse {
        val upk = upkRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("UPK with id $id not found") }

        request.name?.let { upk.name = it }
        request.region?.let { upk.region = it }
        request.bossId?.let { bossId ->
            upk.boss = userRepository.findById(UUID.fromString(bossId))
                .orElseThrow { ResourceNotFoundException("User with id $bossId not found") }
        }

        return upkRepository.save(upk).toResponse()
    }

    @Transactional
    fun delete(id: String) {
        val upk = upkRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("UPK with id $id not found") }
        upkRepository.delete(upk)
    }
}
