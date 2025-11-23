package com.arekalov.papersplease.repository

import com.arekalov.papersplease.model.entity.User
import com.arekalov.papersplease.model.enums.Role
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): User?

    fun findByRole(role: Role): List<User>

    fun existsByEmail(email: String): Boolean

    fun findByUpk_Id(upkId: UUID): List<User>

    fun findByUpk_Id(upkId: UUID, pageable: Pageable): Page<User>

    fun findByRoleAndUpk_Id(role: Role, upkId: UUID): List<User>

    fun deleteByUpk_Id(upkId: UUID)
}
