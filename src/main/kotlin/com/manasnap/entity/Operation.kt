package com.manasnap.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.Instant
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity
@Table(name = "operations")
@EntityListeners(AuditingEntityListener::class)
class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: String? = null

    @Enumerated(EnumType.STRING)
    var status: OperationStatus = OperationStatus.PROCESSING

    // TODO: need to add separate methods like addCardResult, removeCardResult to handle relation
    @OneToMany(mappedBy = "operation", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var cardResults: MutableList<CardResult> = mutableListOf()

    @CreatedDate
    @Column(updatable = false)
    var createdAt: Instant? = null

    @LastModifiedDate
    var lastUpdatedAt: Instant? = null
}