package com.arekalov.papersplease.service

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.user.UserRequest
import com.arekalov.papersplease.dto.user.UserRequestPartial
import com.arekalov.papersplease.dto.user.UserResponse
import com.arekalov.papersplease.exception.ConflictException
import com.arekalov.papersplease.exception.ResourceNotFoundException
import com.arekalov.papersplease.mapper.toResponse
import com.arekalov.papersplease.model.entity.User
import com.arekalov.papersplease.model.enums.Role
import com.arekalov.papersplease.repository.UpkRepository
import com.arekalov.papersplease.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val upkRepository: UpkRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    @Transactional(readOnly = true)
    fun getAll(limit: Int, offset: Int): PagedResponse<UserResponse> {
        val pageable = PageRequest.of(offset / limit, limit)
        val page = userRepository.findAll(pageable)

        return PagedResponse(
            items = page.content.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional(readOnly = true)
    fun getById(id: String): UserResponse {
        val user = userRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("User with id $id not found") }
        return user.toResponse()
    }

    @Transactional(readOnly = true)
    fun getByRole(role: Role, limit: Int, offset: Int): PagedResponse<UserResponse> {
        val users = userRepository.findByRole(role)
        val totalCount = users.size.toLong()

        val paginatedUsers = users
            .drop(offset)
            .take(limit)

        return PagedResponse(
            items = paginatedUsers.map { it.toResponse() },
            total = totalCount,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional
    fun create(request: UserRequest): UserResponse {
        if (userRepository.findByEmail(request.email) != null) {
            throw ConflictException("User with email ${request.email} already exists")
        }

        val upk = request.upkId?.let {
            upkRepository.findById(UUID.fromString(it))
                .orElseThrow { ResourceNotFoundException("UPK with id $it not found") }
        }

        val user = User(
            name = request.name,
            email = request.email,
            passwordHash = passwordEncoder.encode("defaultPassword123"),
            role = request.role,
            upk = upk,
        )

        return userRepository.save(user).toResponse()
    }

    @Transactional
    fun update(id: String, request: UserRequest): UserResponse {
        val user = userRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("User with id $id not found") }

        if (request.email != user.email && userRepository.findByEmail(request.email) != null) {
            throw ConflictException("User with email ${request.email} already exists")
        }

        val upk = request.upkId?.let {
            upkRepository.findById(UUID.fromString(it))
                .orElseThrow { ResourceNotFoundException("UPK with id $it not found") }
        }

        user.apply {
            name = request.name
            email = request.email
            role = request.role
            this.upk = upk
        }

        return userRepository.save(user).toResponse()
    }

    @Transactional
    fun partialUpdate(id: String, request: UserRequestPartial): UserResponse {
        val user = userRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("User with id $id not found") }

        request.email?.let { newEmail ->
            if (newEmail != user.email && userRepository.findByEmail(newEmail) != null) {
                throw ConflictException("User with email $newEmail already exists")
            }
            user.email = newEmail
        }

        request.name?.let { user.name = it }
        request.role?.let { user.role = it }
        request.upkId?.let { upkId ->
            user.upk = upkRepository.findById(UUID.fromString(upkId))
                .orElseThrow { ResourceNotFoundException("UPK with id $upkId not found") }
        }

        return userRepository.save(user).toResponse()
    }

    @Transactional
    fun delete(id: String) {
        val user = userRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("User with id $id not found") }
        userRepository.delete(user)
    }
}
