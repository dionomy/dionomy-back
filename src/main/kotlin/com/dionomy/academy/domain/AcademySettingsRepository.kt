package com.dionomy.academy.domain

import java.util.UUID

interface AcademySettingsRepository {
    fun get(tenantId: UUID): AcademySettings
    fun save(settings: AcademySettings): AcademySettings
}
