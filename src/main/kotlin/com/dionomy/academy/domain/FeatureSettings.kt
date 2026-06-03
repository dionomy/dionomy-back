package com.dionomy.academy.domain

data class FeatureSettings(
    val ownerScheduleEnabled: Boolean,
    val ownerStudentsEnabled: Boolean,
    val ownerNoticesEnabled: Boolean,
    val teacherModeEnabled: Boolean,
    val studentPassEnabled: Boolean,
    val studentClassNotesEnabled: Boolean,
    val studentAbsenceRequestEnabled: Boolean,
    val crmEnabled: Boolean,
)
