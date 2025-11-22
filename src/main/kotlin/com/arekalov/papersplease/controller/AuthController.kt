package com.arekalov.papersplease.controller

import com.arekalov.papersplease.dto.auth.AuthResponse
import com.arekalov.papersplease.dto.auth.LoginRequest
import com.arekalov.papersplease.dto.auth.RefreshRequest
import com.arekalov.papersplease.dto.auth.RegisterRequest
import com.arekalov.papersplease.dto.auth.ResetPasswordRequest
import com.arekalov.papersplease.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
) {

    @PostMapping("/register")
    suspend fun register(
        @Valid @RequestBody request: RegisterRequest,
    ): ResponseEntity<AuthResponse> {
        val response = authService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/login")
    suspend fun login(
        @Valid @RequestBody request: LoginRequest,
    ): ResponseEntity<AuthResponse> {
        val response = authService.login(request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/refresh")
    suspend fun refresh(
        @Valid @RequestBody request: RefreshRequest,
    ): ResponseEntity<AuthResponse> {
        val response = authService.refresh(request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/reset-password")
    suspend fun resetPassword(
        @Valid @RequestBody request: ResetPasswordRequest,
    ): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf("message" to "Password reset link sent to ${request.email}"))
    }
}
