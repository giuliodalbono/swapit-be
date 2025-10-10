package io.github.giuliodalbono.swapit.model.entity

import jakarta.persistence.*
import org.hibernate.annotations.Generated
import org.hibernate.generator.EventType
import java.io.Serializable
import java.time.LocalDateTime

@Entity
@Table(name = "skill_desired")
class SkillDesired: Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Version
    var version: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uid", nullable = false)
    var user: User? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    var skill: Skill? = null

    @Generated(event = [EventType.INSERT])
    @Column(updatable = false, nullable = false)
    lateinit var creationTime: LocalDateTime

    @Generated(event = [EventType.INSERT, EventType.UPDATE])
    @Column(nullable = false)
    lateinit var lastUpdate: LocalDateTime

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

        other as SkillDesired

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "SkillDesired(id=$id, version=$version, user=${user?.uid}, skill=${skill?.label}, creationTime=$creationTime, lastUpdate=$lastUpdate)"
    }
}
