package com.manasnap.repository

import com.manasnap.entity.Operation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface OperationRepository : JpaRepository<Operation, String> {
    @Query("SELECT o FROM Operation o LEFT JOIN FETCH o.cardResults cr WHERE o.id = :operationId")
    fun findByIdWithCardResult(@Param("operationId") operationId: String): Operation?
}
