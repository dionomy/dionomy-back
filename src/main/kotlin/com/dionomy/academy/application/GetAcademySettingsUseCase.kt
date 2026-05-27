package com.dionomy.academy.application

import com.dionomy.academy.domain.AcademySettings
import com.dionomy.academy.domain.AcademySettingsRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetAcademySettingsUseCase(
    private val academySettingsRepository: AcademySettingsRepository,
) {
    fun execute(tenantId: UUID): AcademySettings = academySettingsRepository.get(tenantId)
}
