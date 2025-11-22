package com.arekalov.papersplease.controller

import com.arekalov.papersplease.dto.PagedResponse
import com.arekalov.papersplease.dto.user.UserRequest
import com.arekalov.papersplease.dto.user.UserRequestPartial
import com.arekalov.papersplease.dto.user.UserResponse
import com.arekalov.papersplease.model.enums.Role
import com.arekalov.papersplease.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasAnyRole('BOSS', 'SECURITY', 'GOD')")
class UserController(
    private val userService: UserService,
) {

    @GetMapping
    suspend fun getAllUsers(
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<UserResponse>> {
        val response = userService.getAll(limit, offset)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    suspend fun getUserById(
        @PathVariable id: String,
    ): ResponseEntity<UserResponse> {
        val response = userService.getById(id)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/by-role/{role}")
    suspend fun getUsersByRole(
        @PathVariable role: Role,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<UserResponse>> {
        val response = userService.getByRole(role, limit, offset)
        return ResponseEntity.ok(response)
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('BOSS', 'GOD')")
    suspend fun createUser(
        @Valid @RequestBody request: UserRequest,
    ): ResponseEntity<UserResponse> {
        val response = userService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('BOSS', 'GOD')")
    suspend fun updateUser(
        @PathVariable id: String,
        @Valid @RequestBody request: UserRequest,
    ): ResponseEntity<UserResponse> {
        val response = userService.update(id, request)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('BOSS', 'GOD')")
    suspend fun partialUpdateUser(
        @PathVariable id: String,
        @Valid @RequestBody request: UserRequestPartial,
    ): ResponseEntity<UserResponse> {
        val response = userService.partialUpdate(id, request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GOD')")
    suspend fun deleteUser(
        @PathVariable id: String,
    ): ResponseEntity<Void> {
        userService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
