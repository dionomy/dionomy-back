package com.dionomy.student.domain

import java.util.UUID

interface StudentRepository {
    fun save(student: Student): Student
    fun findByTenant(tenantId: UUID): List<Student>
    fun findByTenantAndId(tenantId: UUID, studentId: UUID): Student?
}
