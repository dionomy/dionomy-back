package com.dionomy.academy.domain

import java.util.UUID

data class AcademySettings(
    val tenantId: UUID,
    val name: String,
    val contact: String,
    val address: String,
    val branding: Branding,
    val passPolicy: PassPolicy,
    val makeupPolicy: MakeupPolicy,
    val featureSettings: FeatureSettings,
)
