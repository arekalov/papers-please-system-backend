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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    suspend fun getAll(limit: Int, offset: Int): PagedResponse<UpkResponse> = withContext(Dispatchers.IO) {
        val pageable = PageRequest.of(offset / limit, limit)
        val page = upkRepository.findAll(pageable)

        PagedResponse(
            items = page.content.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional(readOnly = true)
    suspend fun getById(id: String): UpkResponse = withContext(Dispatchers.IO) {
        val upk = upkRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("UPK with id $id not found") }
        upk.toResponse()
    }

    @Transactional(readOnly = true)
    suspend fun getByRegion(region: Region, limit: Int, offset: Int): PagedResponse<UpkResponse> =
        withContext(Dispatchers.IO) {
            val upks = upkRepository.findByRegion(region)
            val totalCount = upks.size.toLong()

            val paginatedUpks = upks
                .drop(offset)
                .take(limit)

            PagedResponse(
                items = paginatedUpks.map { it.toResponse() },
                total = totalCount,
                limit = limit,
                offset = offset,
            )
        }

    @Transactional
    suspend fun create(request: UpkRequest): UpkResponse = withContext(Dispatchers.IO) {
        val boss = request.bossId?.let {
            userRepository.findById(UUID.fromString(it))
                .orElseThrow { ResourceNotFoundException("User with id $it not found") }
        } ?: throw ResourceNotFoundException("Boss is required")

        val upk = Upk(
            name = request.name,
            region = request.region,
            boss = boss,
        )

        upkRepository.save(upk).toResponse()
    }

    @Transactional
    suspend fun update(id: String, request: UpkRequest): UpkResponse = withContext(Dispatchers.IO) {
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

        upkRepository.save(upk).toResponse()
    }

    @Transactional
    suspend fun partialUpdate(id: String, request: UpkRequestPartial): UpkResponse = withContext(Dispatchers.IO) {
        val upk = upkRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("UPK with id $id not found") }

        request.name?.let { upk.name = it }
        request.region?.let { upk.region = it }
        request.bossId?.let { bossId ->
            upk.boss = userRepository.findById(UUID.fromString(bossId))
                .orElseThrow { ResourceNotFoundException("User with id $bossId not found") }
        }

        upkRepository.save(upk).toResponse()
    }

    @Transactional
    suspend fun delete(id: String) = withContext(Dispatchers.IO) {
        val upk = upkRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("UPK with id $id not found") }
        upkRepository.delete(upk)
    }
}
