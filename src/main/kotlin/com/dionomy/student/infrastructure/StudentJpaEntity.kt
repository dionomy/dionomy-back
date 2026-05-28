package com.dionomy.student.infrastructure

import com.dionomy.student.domain.Student
import com.dionomy.student.domain.StudentRepository
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "students")
class StudentJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "tenant_id", nullable = false)
    var tenantId: UUID = UUID.randomUUID(),
    @Column(name = "name", nullable = false)
    var name: String = "",
    @Column(name = "phone", nullable = false)
    var phone: String = "",
    @Column(name = "memo")
    var memo: String? = null,
    @Column(name = "tags", nullable = false)
    var tags: String = "",
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
) {
    fun toDomain(): Student =
        Student(
            id = id,
            tenantId = tenantId,
            name = name,
            phone = phone,
            memo = memo,
            tags = tags.split(",").map { it.trim() }.filter { it.isNotBlank() },
            createdAt = createdAt,
        )

    companion object {
        fun fromDomain(student: Student): StudentJpaEntity =
            StudentJpaEntity(
                id = student.id,
                tenantId = student.tenantId,
                name = student.name,
                phone = student.phone,
                memo = student.memo,
                tags = student.tags.joinToString(","),
                createdAt = student.createdAt,
            )
    }
}

interface SpringDataStudentJpaRepository : JpaRepository<StudentJpaEntity, UUID> {
    fun findByTenantIdOrderByNameAsc(tenantId: UUID): List<StudentJpaEntity>
    fun findByTenantIdAndId(tenantId: UUID, id: UUID): StudentJpaEntity?
}

@Repository
class JpaStudentRepository(
    private val springDataRepository: SpringDataStudentJpaRepository,
) : StudentRepository {
    override fun save(student: Student): Student =
        springDataRepository.save(StudentJpaEntity.fromDomain(student)).toDomain()

    override fun findByTenant(tenantId: UUID): List<Student> =
        springDataRepository.findByTenantIdOrderByNameAsc(tenantId).map { it.toDomain() }

    override fun findByTenantAndId(tenantId: UUID, studentId: UUID): Student? =
        springDataRepository.findByTenantIdAndId(tenantId, studentId)?.toDomain()
}
