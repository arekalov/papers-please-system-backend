package com.arekalov.papersplease.service

import com.arekalov.papersplease.dto.auth.AuthResponse
import com.arekalov.papersplease.dto.auth.LoginRequest
import com.arekalov.papersplease.dto.auth.RefreshRequest
import com.arekalov.papersplease.dto.auth.RegisterRequest
import com.arekalov.papersplease.exception.ConflictException
import com.arekalov.papersplease.exception.ResourceNotFoundException
import com.arekalov.papersplease.exception.UnauthorizedException
import com.arekalov.papersplease.mapper.toResponse
import com.arekalov.papersplease.model.entity.User
import com.arekalov.papersplease.model.enums.Role
import com.arekalov.papersplease.repository.UpkRepository
import com.arekalov.papersplease.repository.UserRepository
import com.arekalov.papersplease.security.JwtTokenProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val upkRepository: UpkRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
) {

    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
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
            passwordHash = passwordEncoder.encode(request.password),
            role = Role.MIGRANT,
            upk = upk,
        )

        val savedUser = userRepository.save(user)

        val accessToken = jwtTokenProvider.generateAccessToken(savedUser.id.toString(), savedUser.role.name)
        val refreshToken = jwtTokenProvider.generateRefreshToken(savedUser.id.toString())

        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = savedUser.toResponse(),
        )
    }

    @Transactional(readOnly = true)
    fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw UnauthorizedException("Invalid email or password")

        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw UnauthorizedException("Invalid email or password")
        }

        val accessToken = jwtTokenProvider.generateAccessToken(user.id.toString(), user.role.name)
        val refreshToken = jwtTokenProvider.generateRefreshToken(user.id.toString())

        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = user.toResponse(),
        )
    }

    @Transactional(readOnly = true)
    fun refresh(request: RefreshRequest): AuthResponse {
        if (!jwtTokenProvider.validateToken(request.refreshToken)) {
            throw UnauthorizedException("Invalid refresh token")
        }

        val userId = jwtTokenProvider.getUserIdFromToken(request.refreshToken)
        val user = userRepository.findById(UUID.fromString(userId))
            .orElseThrow { ResourceNotFoundException("User not found") }

        val accessToken = jwtTokenProvider.generateAccessToken(user.id.toString(), user.role.name)
        val newRefreshToken = jwtTokenProvider.generateRefreshToken(user.id.toString())

        return AuthResponse(
            accessToken = accessToken,
            refreshToken = newRefreshToken,
            user = user.toResponse(),
        )
    }
}
