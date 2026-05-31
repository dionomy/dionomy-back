package com.dionomy.operation.domain

import java.util.UUID

class Instructor(
    val id: UUID,
    val tenantId: UUID,
    val name: String,
    val phone: String?,
) {
    init {
        require(name.isNotBlank())
    }
}

interface InstructorRepository {
    fun findByTenant(tenantId: UUID): List<Instructor>
    fun save(instructor: Instructor): Instructor
}
