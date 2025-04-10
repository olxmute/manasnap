package com.manasnap.repository

import com.manasnap.entity.Operation
import org.springframework.data.jpa.repository.JpaRepository

interface OperationRepository : JpaRepository<Operation, String>
