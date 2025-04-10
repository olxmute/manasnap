package com.manasnap.repository

import com.manasnap.entity.CardResult
import org.springframework.data.jpa.repository.JpaRepository

interface CardResultRepository : JpaRepository<CardResult, Long>