package com.manasnap.entity

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "card_results")
class CardResult {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: String? = null

    var cardName: String = ""
    var pngUrl: String? = null
    var error: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_id")
    var operation: Operation? = null
}