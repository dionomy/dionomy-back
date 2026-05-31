package com.dionomy.operation.domain

import java.time.LocalDateTime
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

class InstructorAvailability(
    val id: UUID,
    val tenantId: UUID,
    val instructorId: UUID,
    val startsAt: LocalDateTime,
    val endsAt: LocalDateTime,
    val memo: String?,
) {
    init {
        require(endsAt.isAfter(startsAt))
    }

    fun covers(startsAt: LocalDateTime, endsAt: LocalDateTime): Boolean =
        !this.startsAt.isAfter(startsAt) && !this.endsAt.isBefore(endsAt)
}

interface InstructorAvailabilityRepository {
    fun findByTenant(tenantId: UUID): List<InstructorAvailability>
    fun findByInstructor(tenantId: UUID, instructorId: UUID): List<InstructorAvailability>
    fun findByTenantAndId(tenantId: UUID, availabilityId: UUID): InstructorAvailability?
    fun save(availability: InstructorAvailability): InstructorAvailability
}
