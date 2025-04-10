package com.manasnap.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class CardResult(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: String? = null,

    var cardName: String = "",
    var pngUrl: String? = null,
    var error: String? = null,

    @ManyToOne
    @JoinColumn(name = "operation_id")
    var operation: Operation? = null

)