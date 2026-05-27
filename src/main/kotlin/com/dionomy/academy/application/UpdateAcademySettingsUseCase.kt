package com.dionomy.academy.application

import com.dionomy.academy.domain.AcademySettings
import com.dionomy.academy.domain.AcademySettingsRepository
import org.springframework.stereotype.Service

@Service
class UpdateAcademySettingsUseCase(
    private val academySettingsRepository: AcademySettingsRepository,
) {
    fun execute(settings: AcademySettings): AcademySettings = academySettingsRepository.save(settings)
}
