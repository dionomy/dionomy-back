package com.dionomy.schedule.domain

import java.time.LocalDateTime
import java.util.UUID

class ClassSession(
    val id: UUID,
    val tenantId: UUID,
    val title: String,
    val type: ClassType,
    val teacherId: UUID,
    val placeId: UUID?,
    val startsAt: LocalDateTime,
    val endsAt: LocalDateTime,
    val capacity: SessionCapacity,
    val assignedStudentIds: List<UUID> = emptyList(),
    val recurrence: RecurrenceRule? = null,
) {
    init {
        require(title.isNotBlank())
        require(endsAt.isAfter(startsAt))
        require(assignedStudentIds.distinct().size == assignedStudentIds.size)
    }

    fun overlaps(other: ClassSession): Boolean =
        startsAt.isBefore(other.endsAt) && endsAt.isAfter(other.startsAt)

    fun conflictsWith(other: ClassSession): Boolean {
        if (tenantId != other.tenantId || id == other.id || !overlaps(other)) {
            return false
        }

        return teacherId == other.teacherId || (placeId != null && placeId == other.placeId)
    }
}
