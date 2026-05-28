package com.dionomy.schedule.domain

class ScheduleConflictPolicy {
    fun validate(candidate: ClassSession, existingSessions: List<ClassSession>) {
        val conflict = findConflict(candidate, existingSessions)
        require(conflict == null) { "Schedule conflict: ${conflict?.reason}" }
    }

    fun findConflict(candidate: ClassSession, existingSessions: List<ClassSession>): ScheduleConflict? =
        existingSessions.firstNotNullOfOrNull { existing ->
            if (!candidate.overlaps(existing) || candidate.tenantId != existing.tenantId || candidate.id == existing.id) {
                return@firstNotNullOfOrNull null
            }

            when {
                candidate.teacherId == existing.teacherId -> ScheduleConflict(existing.id, ScheduleConflictReason.TEACHER_OVERLAP)
                candidate.placeId != null && candidate.placeId == existing.placeId -> ScheduleConflict(existing.id, ScheduleConflictReason.PLACE_OVERLAP)
                else -> null
            }
        }
}
