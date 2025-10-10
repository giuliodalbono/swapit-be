package io.github.giuliodalbono.swapit.model.entity

import jakarta.persistence.*
import org.hibernate.annotations.Generated
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.generator.EventType
import org.hibernate.type.SqlTypes
import java.io.Serializable
import java.time.LocalDateTime

@Entity
@Table(name = "user")
class User: Serializable {
    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    var uid: String? = null

    @Version
    var version: Long = 0

    @Column(nullable = false, unique = true)
    var email: String? = null

    @Column(nullable = false)
    var username: String? = null

    @Lob
    @Column(columnDefinition = "BLOB")
    var profilePicture: ByteArray? = null

    @Generated(event = [EventType.INSERT])
    @Column(updatable = false, nullable = false)
    lateinit var creationTime: LocalDateTime

    @Generated(event = [EventType.INSERT, EventType.UPDATE])
    @Column(nullable = false)
    lateinit var lastUpdate: LocalDateTime

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    var skillDesired: Set<SkillDesired> = emptySet()

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
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

        other as User

        return uid == other.uid
    }

    override fun hashCode(): Int {
        return uid.hashCode()
    }

    override fun toString(): String {
        return "User(uid='$uid', email='$email', username='$username', version=$version, creationTime=$creationTime, lastUpdate=$lastUpdate)"
    }
}