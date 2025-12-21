package com.arekalov.papersplease.service

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.user.UserDetailedResponse
import com.arekalov.papersplease.dto.user.UserFullDetailedResponse
import com.arekalov.papersplease.dto.user.UserParticipationInfo
import com.arekalov.papersplease.dto.user.UserRequest
import com.arekalov.papersplease.dto.user.UserRequestPartial
import com.arekalov.papersplease.dto.user.UserResponse
import com.arekalov.papersplease.dto.user.UserShiftInfo
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
    private val shiftRepository: com.arekalov.papersplease.repository.ShiftRepository,
    private val participationRepository: com.arekalov.papersplease.repository.ParticipationRepository,
    private val ticketRepository: com.arekalov.papersplease.repository.TicketRepository,
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
    fun getUsersByUpk(currentUserId: String?, upkId: String): List<UserResponse> {
        val currentUser = currentUserId?.let {
            userRepository.findById(UUID.fromString(it))
                .orElseThrow { ResourceNotFoundException("Current user not found") }
        }

        val targetUpkId = UUID.fromString(upkId)

        upkRepository.findById(targetUpkId)
            .orElseThrow { ResourceNotFoundException("UPK with id $upkId not found") }

        if (currentUser?.role == Role.BOSS) {
            val currentUserUpkId = currentUser.upk?.id
                ?: throw ForbiddenException("Boss must be assigned to UPK")
            if (targetUpkId != currentUserUpkId) {
                throw ForbiddenException("Boss can only get users from their own UPK")
            }
        }

        val users = userRepository.findUsersByUpkUsingFunction(targetUpkId)

        return users.map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getUsersByUpkBossOnly(currentUserId: String?, upkId: String): List<UserResponse> {
        val currentUser = currentUserId?.let {
            userRepository.findById(UUID.fromString(it))
                .orElseThrow { ResourceNotFoundException("Current user not found") }
        } ?: throw ForbiddenException("Authentication required")

        val targetUpkId = UUID.fromString(upkId)

        upkRepository.findById(targetUpkId)
            .orElseThrow { ResourceNotFoundException("UPK with id $upkId not found") }

        if (currentUser.role == Role.BOSS) {
            val currentUserUpkId = currentUser.upk?.id
                ?: throw ForbiddenException("Boss must be assigned to UPK")
            if (targetUpkId != currentUserUpkId) {
                throw ForbiddenException("Boss can only get users from their own UPK")
            }
        } else if (currentUser.role != Role.GOD) {
            throw ForbiddenException("Only Boss of the UPK or GOD can view UPK employees")
        }

        val users = userRepository.findUsersByUpkUsingFunction(targetUpkId)

        return users.map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getById(currentUserId: String?, id: String): UserResponse {
        val user = userRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("User with id $id not found") }

        currentUserId?.let { checkAccessToUser(it, user) }

        return user.toResponse()
    }

    @Transactional(readOnly = true)
    fun getDetailedById(currentUserId: String?, id: String): UserDetailedResponse {
        val user = userRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("User with id $id not found") }

        if (user.role != Role.BOSS) {
            throw ForbiddenException("This endpoint is only available for users with BOSS role")
        }

        currentUserId?.let { checkAccessToUser(it, user) }

        val subordinates = user.upk?.id?.let { upkId ->
            userRepository.findByUpk_Id(upkId)
                .filter { it.id != user.id && it.role != Role.MIGRANT }
                .map { it.toResponse() }
        } ?: emptyList()

        val shifts = shiftRepository.findByCreatedBy_Id(user.id!!).map { it.toResponse() }

        return UserDetailedResponse(
            id = user.id.toString(),
            name = user.name,
            email = user.email,
            role = user.role,
            upk = user.upk?.toResponse(),
            subordinates = subordinates,
            shifts = shifts,
        )
    }

    @Transactional(readOnly = true)
    fun getFullDetailedById(currentUserId: String?, id: String): UserFullDetailedResponse {
        val user = userRepository.findById(UUID.fromString(id))
            .orElseThrow { ResourceNotFoundException("User with id $id not found") }

        currentUserId?.let { checkAccessToUser(it, user) }

        val boss = user.upk?.id?.let { upkId ->
            userRepository.findByRoleAndUpk_Id(Role.BOSS, upkId).firstOrNull()
        }

        val participations = participationRepository.findByUser_Id(user.id!!)

        val shifts = participations.map { participation ->
            val shift = participation.shift
            val shiftBoss = userRepository.findByRoleAndUpk_Id(Role.BOSS, shift.upk.id!!)
                .firstOrNull() ?: throw ResourceNotFoundException("Boss not found for UPK ${shift.upk.id}")

            val resolvedTickets = ticketRepository.findByExecutor_Id(
                user.id!!,
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

            val participationInfo = UserParticipationInfo(
                userId = user.id.toString(),
                shiftId = shift.id.toString(),
                wage = participation.wage,
                penalty = participation.penalty,
                specialization = participation.specialization,
                resolvedTickets = resolvedTickets,
            )

            UserShiftInfo(
                id = shift.id.toString(),
                startTime = shift.startTime,
                endTime = shift.endTime,
                createdBy = shift.createdBy.id.toString(),
                upkId = shift.upk.id.toString(),
                boss = shiftBoss.toResponse(),
                upk = shift.upk.toResponse(),
                participation = participationInfo,
            )
        }

        return UserFullDetailedResponse(
            id = user.id.toString(),
            name = user.name,
            email = user.email,
            passwordHash = null,
            role = user.role,
            upk = user.upk?.toResponse(),
            boss = boss?.toResponse(),
            shifts = shifts,
        )
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

        if (currentUser.id == targetUser.id) {
            return
        }

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
