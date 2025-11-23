package com.arekalov.papersplease.controller

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.user.UserRequest
import com.arekalov.papersplease.dto.user.UserRequestPartial
import com.arekalov.papersplease.dto.user.UserResponse
import com.arekalov.papersplease.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
) {

    @GetMapping
    @PreAuthorize("hasAnyRole('BOSS', 'GOD')")
    fun getAllUsers(
        authentication: Authentication,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<UserResponse>> {
        val response = userService.getAll(authentication.name, limit, offset)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/me")
    fun getCurrentUser(
        authentication: Authentication,
    ): ResponseEntity<UserResponse> {
        val response = userService.getById(null, authentication.name)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/me")
    fun partialUpdateCurrentUser(
        authentication: Authentication,
        @Valid @RequestBody request: UserRequestPartial,
    ): ResponseEntity<UserResponse> {
        val response = userService.partialUpdate(null, authentication.name, request)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('BOSS', 'SECURITY', 'GOD')")
    fun getUserById(
        authentication: Authentication,
        @PathVariable id: String,
    ): ResponseEntity<UserResponse> {
        val response = userService.getById(authentication.name, id)
        return ResponseEntity.ok(response)
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('BOSS', 'GOD')")
    fun createUser(
        authentication: Authentication,
        @Valid @RequestBody request: UserRequest,
    ): ResponseEntity<UserResponse> {
        val response = userService.create(authentication.name, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('BOSS', 'GOD')")
    fun partialUpdateUser(
        authentication: Authentication,
        @PathVariable id: String,
        @Valid @RequestBody request: UserRequestPartial,
    ): ResponseEntity<UserResponse> {
        val response = userService.partialUpdate(authentication.name, id, request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('BOSS', 'GOD')")
    fun deleteUser(
        authentication: Authentication,
        @PathVariable id: String,
    ): ResponseEntity<Unit> {
        userService.delete(authentication.name, id)
        return ResponseEntity.noContent().build()
    }
}
