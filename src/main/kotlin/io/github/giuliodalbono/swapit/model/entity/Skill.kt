package io.github.giuliodalbono.swapit.model.entity

import jakarta.persistence.*
import org.hibernate.annotations.Generated
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.generator.EventType
import org.hibernate.type.SqlTypes
import java.io.Serializable
import java.time.LocalDateTime
import java.util.HashMap

@Entity
@Table(name = "skill")
class Skill: Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Version
    var version: Long = 0

    @Column(nullable = false, unique = true)
    var label: String? = null

    @Column(length = 10 * 1024 * 1024)
    @JdbcTypeCode(SqlTypes.JSON)
    var metadata: MutableMap<String, String>? = HashMap()

    @Column
    var description: String? = null

    @Generated(event = [EventType.INSERT])
    @Column(updatable = false, nullable = false)
    lateinit var creationTime: LocalDateTime

    @Generated(event = [EventType.INSERT, EventType.UPDATE])
    @Column(nullable = false)
    lateinit var lastUpdate: LocalDateTime

    @OneToMany(mappedBy = "skill", cascade = [CascadeType.ALL])
    var skillDesired: Set<SkillDesired> = emptySet()

    @OneToMany(mappedBy = "skill", cascade = [CascadeType.ALL])
    var skillOffered: Set<SkillOffered> = emptySet()

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

        other as Skill

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Skill(id=$id, version=$version, label=$label, metadata=$metadata, description=$description, creationTime=$creationTime, lastUpdate=$lastUpdate)"
    }
}