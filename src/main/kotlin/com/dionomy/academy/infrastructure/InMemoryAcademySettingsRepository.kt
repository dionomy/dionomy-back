package com.dionomy.academy.infrastructure

import com.dionomy.academy.domain.AcademySettings
import com.dionomy.academy.domain.AcademySettingsRepository
import com.dionomy.academy.domain.Branding
import com.dionomy.academy.domain.MakeupPolicy
import com.dionomy.academy.domain.PassPolicy
import org.springframework.stereotype.Repository
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Repository
class InMemoryAcademySettingsRepository : AcademySettingsRepository {
    private val store = ConcurrentHashMap<UUID, AcademySettings>()

    override fun get(tenantId: UUID): AcademySettings =
        store.computeIfAbsent(tenantId) {
            AcademySettings(
                tenantId = tenantId,
                name = "샘플 아카데미",
                contact = "02-000-0000",
                address = "서울시 강남구",
                branding = Branding(
                    logoUrl = null,
                    mainColor = "#4F46E5",
                ),
                passPolicy = PassPolicy(
                    extensionAllowed = true,
                    refundAllowed = false,
                ),
                makeupPolicy = MakeupPolicy(
                    enabled = true,
                    expiresInDays = 30,
                    maxCount = 2,
                ),
            )
        }

    override fun save(settings: AcademySettings): AcademySettings {
        store[settings.tenantId] = settings
        return settings
    }
}
