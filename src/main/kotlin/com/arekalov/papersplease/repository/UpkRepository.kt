package com.arekalov.papersplease.repository

import com.arekalov.papersplease.model.entity.Upk
import com.arekalov.papersplease.model.enums.Region
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UpkRepository : JpaRepository<Upk, UUID> {
    fun findByRegion(region: Region): List<Upk>
}
