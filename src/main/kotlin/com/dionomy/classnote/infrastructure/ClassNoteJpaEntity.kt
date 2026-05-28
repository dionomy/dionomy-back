package com.dionomy.classnote.infrastructure

import com.dionomy.classnote.domain.ClassNote
import com.dionomy.classnote.domain.ClassNoteRepository
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "class_notes")
class ClassNoteJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "tenant_id", nullable = false)
    var tenantId: UUID = UUID.randomUUID(),
    @Column(name = "session_id", nullable = false)
    var sessionId: UUID = UUID.randomUUID(),
    @Column(name = "teacher_id", nullable = false)
    var teacherId: UUID = UUID.randomUUID(),
    @Column(name = "progress", nullable = false)
    var progress: String = "",
    @Column(name = "feedback", nullable = false)
    var feedback: String = "",
    @Column(name = "next_assignment", nullable = false)
    var nextAssignment: String = "",
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
) {
    fun toDomain(): ClassNote =
        ClassNote(
            id = id,
            tenantId = tenantId,
            sessionId = sessionId,
            teacherId = teacherId,
            progress = progress,
            feedback = feedback,
            nextAssignment = nextAssignment,
            createdAt = createdAt,
        )

    companion object {
        fun fromDomain(note: ClassNote): ClassNoteJpaEntity =
            ClassNoteJpaEntity(
                id = note.id,
                tenantId = note.tenantId,
                sessionId = note.sessionId,
                teacherId = note.teacherId,
                progress = note.progress,
                feedback = note.feedback,
                nextAssignment = note.nextAssignment,
                createdAt = note.createdAt,
            )
    }
}

interface SpringDataClassNoteJpaRepository : JpaRepository<ClassNoteJpaEntity, UUID> {
    fun findByTenantIdOrderByCreatedAtDesc(tenantId: UUID): List<ClassNoteJpaEntity>
    fun findByTenantIdAndSessionIdOrderByCreatedAtDesc(tenantId: UUID, sessionId: UUID): List<ClassNoteJpaEntity>
}

@Repository
class JpaClassNoteRepository(
    private val springDataRepository: SpringDataClassNoteJpaRepository,
) : ClassNoteRepository {
    override fun save(note: ClassNote): ClassNote =
        springDataRepository.save(ClassNoteJpaEntity.fromDomain(note)).toDomain()

    override fun findByTenantAndSession(tenantId: UUID, sessionId: UUID): List<ClassNote> =
        springDataRepository.findByTenantIdAndSessionIdOrderByCreatedAtDesc(tenantId, sessionId).map { it.toDomain() }

    override fun findByTenant(tenantId: UUID): List<ClassNote> =
        springDataRepository.findByTenantIdOrderByCreatedAtDesc(tenantId).map { it.toDomain() }
}
