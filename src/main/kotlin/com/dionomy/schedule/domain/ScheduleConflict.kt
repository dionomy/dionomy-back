package com.dionomy.schedule.domain

import java.util.UUID

data class ScheduleConflict(
    val sessionId: UUID,
    val reason: ScheduleConflictReason,
)

enum class ScheduleConflictReason {
    TEACHER_OVERLAP,
    PLACE_OVERLAP,
}
