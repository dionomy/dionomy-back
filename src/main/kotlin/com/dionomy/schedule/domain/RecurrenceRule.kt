package com.dionomy.schedule.domain

import java.time.DayOfWeek
import java.time.LocalDate

data class RecurrenceRule(
    val frequency: RecurrenceFrequency,
    val daysOfWeek: Set<DayOfWeek>,
    val until: LocalDate,
) {
    init {
        require(daysOfWeek.isNotEmpty())
    }
}

enum class RecurrenceFrequency {
    WEEKLY,
}
