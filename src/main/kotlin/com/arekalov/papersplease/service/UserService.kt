package com.arekalov.papersplease.service

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.user.UserRequest
import com.arekalov.papersplease.dto.user.UserRequestPartial
import com.arekalov.papersplease.dto.user.UserResponse
import com.arekalov.papersplease.exception.ConflictException
import com.arekalov.papersplease.exception.ForbiddenException
import com.arekalov.papersplease.exception.ResourceNotFoundException
import com.arekalov.papersplease.mapper.toResponse
import com.arekalov.papersplease.model.entity.Upk
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
    fun getAll(currentUserId: String?, limit: Int, offset: Int): PagedResponse<UserResponse> {
        val pageable = PageRequest.of(offset / limit, limit)

        val currentUser = currentUserId?.let {
            userRepository.findById(UUID.fromString(it)).orElse(null)
        }

        val page = if (currentUser?.role == Role.BOSS) {
            val upkId = currentUser.upk?.id
            if (upkId != null) {
                userRepository.findByUpk_Id(upkId, pageable)
            } else {
                userRepository.findAll(pageable)
            }
        } else {
            userRepository.findAll(pageable)
        }

        return PagedResponse(
            items = page.content.map { it.toResponse() },
            total = page.totalElements,
            limit = limit,
            offset = offset,
        )
    }

    @Transactional(readOnly = true)
    fun getById(currentUserId: String?, id: String): UserResponse {
        val user = userRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("User with id $id not found") }

        currentUserId?.let { checkAccessToUser(it, user) }

        return user.toResponse()
    }

    @Transactional
    fun create(currentUserId: String?, request: UserRequest): UserResponse {
        if (userRepository.findByEmail(request.email) != null) {
            throw ConflictException("User with email ${request.email} already exists")
        }

        val currentUser = currentUserId?.let {
            userRepository.findById(UUID.fromString(it)).orElse(null)
        }

        val upk = request.upkId?.let {
            upkRepository.findById(UUID.fromString(it))
                .orElseThrow { ResourceNotFoundException("UPK with id $it not found") }
        }

        currentUser?.let { checkBossUpkAccess(it, upk, "create users in") }

        val user = User(
            name = request.name,
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password),
            role = request.role,
            upk = upk,
        )

        return userRepository.save(user).toResponse()
    }

    @Transactional
    fun partialUpdate(currentUserId: String?, id: String, request: UserRequestPartial): UserResponse {
        val user = userRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("User with id $id not found") }

        currentUserId?.let { checkAccessToUser(it, user) }

        request.email?.let { newEmail ->
            if (newEmail != user.email && userRepository.findByEmail(newEmail) != null) {
                throw ConflictException("User with email $newEmail already exists")
            }
            user.email = newEmail
        }

        val currentUser = currentUserId?.let {
            userRepository.findById(UUID.fromString(it)).orElse(null)
        }

        request.name?.let { user.name = it }
        request.role?.let { user.role = it }
        request.upkId?.let { upkId ->
            val upk = upkRepository.findById(UUID.fromString(upkId))
                .orElseThrow { ResourceNotFoundException("UPK with id $upkId not found") }

            currentUser?.let { checkBossUpkAccess(it, upk, "assign users") }
            user.upk = upk
        }

        return userRepository.save(user).toResponse()
    }

    @Transactional
    fun delete(currentUserId: String?, id: String) {
        val user = userRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("User with id $id not found") }

        currentUserId?.let { checkAccessToUser(it, user) }

        userRepository.delete(user)
    }

    private fun checkAccessToUser(currentUserId: String, targetUser: User) {
        val currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow { ResourceNotFoundException("Current user not found") }

        if (currentUser.role == Role.BOSS) {
            currentUser.upk?.id?.let { currentUserUpkId ->
                if (targetUser.upk?.id != currentUserUpkId) {
                    throw ForbiddenException("Boss can only access users from their own UPK")
                }
            } ?: throw ForbiddenException("Boss must be assigned to UPK")
        }
    }

    private fun checkBossUpkAccess(currentUser: User, upk: Upk?, action: String) {
        if (currentUser.role == Role.BOSS) {
            val currentUserUpkId = currentUser.upk?.id
                ?: throw ForbiddenException("Boss must be assigned to UPK")

            if (upk?.id != currentUserUpkId) {
                throw ForbiddenException("Boss can only $action to their own UPK")
            }
        }
    }
}
