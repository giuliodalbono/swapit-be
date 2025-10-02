package io.github.giuliodalbono.swapit.model.entity

import jakarta.persistence.*
import org.hibernate.annotations.Generated
import org.hibernate.generator.EventType
import java.io.Serializable
import java.time.LocalDateTime

@Entity
@Table(name = "feedback")
class Feedback: Serializable {
    @Id
    var id: Long? = null

    @Version
    var version: Long = 0

    @Column(nullable = false)
    var rating: Long? = null

    @Column(nullable = false)
    var review: String? = null

    @Generated(event = [EventType.INSERT])
    @Column(updatable = false, nullable = false)
    var creationTime: LocalDateTime? = null

    @Generated(event = [EventType.INSERT, EventType.UPDATE])
    @Column(nullable = false)
    var lastUpdate: LocalDateTime? = null

    @ManyToOne
    var reviewer: User? = null

    @ManyToOne
    var reviewed: User? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Feedback

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Feedback(id=$id, version=$version, rating=$rating, review=$review, creationTime=$creationTime, lastUpdate=$lastUpdate, reviewer=$reviewer, reviewed=$reviewed)"
    }
}