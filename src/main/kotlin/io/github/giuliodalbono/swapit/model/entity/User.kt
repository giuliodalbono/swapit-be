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

    @Generated(event = [EventType.INSERT])
    @Column(updatable = false, nullable = false)
    var creationTime: LocalDateTime? = null

    @Generated(event = [EventType.INSERT, EventType.UPDATE])
    @Column(nullable = false)
    var lastUpdate: LocalDateTime? = null

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