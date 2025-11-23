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
import org.springframework.security.core.Authentication
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
class UserController(
    private val userService: UserService,
) {

    @GetMapping
    @PreAuthorize("hasAnyRole('BOSS', 'SECURITY', 'GOD')")
    fun getAllUsers(
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<UserResponse>> {
        return ResponseEntity.ok(userService.getAll(limit, offset))
    }

    @GetMapping("/me")
    fun getCurrentUser(
        authentication: Authentication,
    ): ResponseEntity<UserResponse> {
        return ResponseEntity.ok(userService.getById(authentication.name))
    }

    @PutMapping("/me")
    fun updateCurrentUser(
        authentication: Authentication,
        @Valid @RequestBody request: UserRequest,
    ): ResponseEntity<UserResponse> {
        return ResponseEntity.ok(userService.update(authentication.name, request))
    }

    @PatchMapping("/me")
    fun partialUpdateCurrentUser(
        authentication: Authentication,
        @Valid @RequestBody request: UserRequestPartial,
    ): ResponseEntity<UserResponse> {
        return ResponseEntity.ok(userService.partialUpdate(authentication.name, request))
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('BOSS', 'SECURITY', 'GOD')")
    fun getUserById(
        @PathVariable id: String,
    ): ResponseEntity<UserResponse> {
        return ResponseEntity.ok(userService.getById(id))
    }

    @GetMapping("/by-role/{role}")
    @PreAuthorize("hasAnyRole('BOSS', 'SECURITY', 'GOD')")
    fun getUsersByRole(
        @PathVariable role: Role,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<PagedResponse<UserResponse>> {
        return ResponseEntity.ok(userService.getByRole(role, limit, offset))
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('BOSS', 'GOD')")
    fun createUser(
        @Valid @RequestBody request: UserRequest,
    ): ResponseEntity<UserResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request))
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('BOSS', 'GOD')")
    fun updateUser(
        @PathVariable id: String,
        @Valid @RequestBody request: UserRequest,
    ): ResponseEntity<UserResponse> {
        return ResponseEntity.ok(userService.update(id, request))
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('BOSS', 'GOD')")
    fun partialUpdateUser(
        @PathVariable id: String,
        @Valid @RequestBody request: UserRequestPartial,
    ): ResponseEntity<UserResponse> {
        return ResponseEntity.ok(userService.partialUpdate(id, request))
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GOD')")
    fun deleteUser(
        @PathVariable id: String,
    ): ResponseEntity<Unit> {
        userService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
