package com.dionomy.student.infrastructure

import com.dionomy.student.domain.Student
import com.dionomy.student.domain.StudentRepository
import org.springframework.stereotype.Repository
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Repository
class InMemoryStudentRepository : StudentRepository {
    private val students = ConcurrentHashMap<UUID, Student>()

    override fun save(student: Student): Student {
        students[student.id] = student
        return student
    }

    override fun findByTenant(tenantId: UUID): List<Student> =
        students.values
            .filter { it.tenantId == tenantId }
            .sortedBy { it.name }

    override fun findByTenantAndId(tenantId: UUID, studentId: UUID): Student? =
        students[studentId]?.takeIf { it.tenantId == tenantId }
}
