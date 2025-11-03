package io.github.giuliodalbono.swapit.model.entity

import io.github.giuliodalbono.swapit.model.SwapProposalStatus
import jakarta.persistence.*
import org.hibernate.annotations.Generated
import org.hibernate.generator.EventType
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity
@Table(name = "swap_proposal")
class SwapProposal: Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Version
    var version: Long = 0

    @Column(nullable = false)
    var date: LocalDate? = null

    @Column(nullable = false)
    var startTime: LocalTime? = null

    @Column(nullable = false)
    var endTime: LocalTime? = null

    @Column
    var presentationLetter: String? = null

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: SwapProposalStatus? = null

    @Generated(event = [EventType.INSERT])
    @Column(updatable = false, nullable = false)
    lateinit var creationTime: LocalDateTime

    @Generated(event = [EventType.INSERT, EventType.UPDATE])
    @Column(nullable = false)
    lateinit var lastUpdate: LocalDateTime

    @ManyToOne
    var skillOffered: Skill? = null

    @ManyToOne
    var skillRequested: Skill? = null

    @ManyToOne
    var requestUser: User? = null

    @ManyToOne
    var offerUser: User? = null

    @PrePersist
    fun prePersist() {
        lastUpdate = LocalDateTime.now()
        if (!this::creationTime.isInitialized) {
            creationTime = lastUpdate
        }
    }

    @PreUpdate
    fun preUpdate() {
        lastUpdate = LocalDateTime.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SwapProposal

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "SwapProposal(id=$id, version=$version, date=$date, startTime=$startTime, endTime=$endTime, presentationLetter=$presentationLetter, status=$status, creationTime=$creationTime, lastUpdate=$lastUpdate, skillOffered=$skillOffered, skillRequested=$skillRequested, requestUser=$requestUser, offerUser=$offerUser)"
    }
}