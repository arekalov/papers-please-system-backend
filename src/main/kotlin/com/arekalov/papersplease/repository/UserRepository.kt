package com.arekalov.papersplease.repository

import com.arekalov.papersplease.model.entity.User
import com.arekalov.papersplease.model.enums.Role
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
@Suppress("ForbiddenComment")
interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): User?

    fun findByRole(role: Role): List<User>

    fun existsByEmail(email: String): Boolean

    fun findByUpk_Id(upkId: UUID): List<User>

    fun findByUpk_Id(upkId: UUID, pageable: Pageable): Page<User>

    fun findByRoleAndUpk_Id(role: Role, upkId: UUID): List<User>

    fun deleteByUpk_Id(upkId: UUID)

    /**
     * Получить всех сотрудников УПК с использованием PL/pgSQL функции
     * Использует функцию get_users_by_upk для оптимизированного получения данных
     */
    @Query(value = "SELECT * FROM get_users_by_upk(CAST(:upkId AS UUID))", nativeQuery = true)
    fun findUsersByUpkUsingFunction(@Param("upkId") upkId: UUID): List<User>
}
