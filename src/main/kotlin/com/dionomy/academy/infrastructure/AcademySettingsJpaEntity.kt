package com.dionomy.academy.infrastructure

import com.dionomy.academy.domain.AcademySettings
import com.dionomy.academy.domain.AcademySettingsRepository
import com.dionomy.academy.domain.Branding
import com.dionomy.academy.domain.MakeupPolicy
import com.dionomy.academy.domain.PassPolicy
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Entity
@Table(name = "academy_settings")
class AcademySettingsJpaEntity(
    @Id
    @Column(name = "tenant_id", nullable = false)
    var tenantId: UUID = UUID.randomUUID(),
    @Column(name = "name", nullable = false)
    var name: String = "",
    @Column(name = "contact", nullable = false)
    var contact: String = "",
    @Column(name = "address", nullable = false)
    var address: String = "",
    @Column(name = "logo_url")
    var logoUrl: String? = null,
    @Column(name = "main_color", nullable = false)
    var mainColor: String = "#4F46E5",
    @Column(name = "extension_allowed", nullable = false)
    var extensionAllowed: Boolean = true,
    @Column(name = "refund_allowed", nullable = false)
    var refundAllowed: Boolean = false,
    @Column(name = "makeup_enabled", nullable = false)
    var makeupEnabled: Boolean = true,
    @Column(name = "makeup_expires_in_days", nullable = false)
    var makeupExpiresInDays: Int = 30,
    @Column(name = "makeup_max_count", nullable = false)
    var makeupMaxCount: Int = 2,
) {
    fun toDomain(): AcademySettings =
        AcademySettings(
            tenantId = tenantId,
            name = name,
            contact = contact,
            address = address,
            branding = Branding(
                logoUrl = logoUrl,
                mainColor = mainColor,
            ),
            passPolicy = PassPolicy(
                extensionAllowed = extensionAllowed,
                refundAllowed = refundAllowed,
            ),
            makeupPolicy = MakeupPolicy(
                enabled = makeupEnabled,
                expiresInDays = makeupExpiresInDays,
                maxCount = makeupMaxCount,
            ),
        )

    companion object {
        fun fromDomain(settings: AcademySettings): AcademySettingsJpaEntity =
            AcademySettingsJpaEntity(
                tenantId = settings.tenantId,
                name = settings.name,
                contact = settings.contact,
                address = settings.address,
                logoUrl = settings.branding.logoUrl,
                mainColor = settings.branding.mainColor,
                extensionAllowed = settings.passPolicy.extensionAllowed,
                refundAllowed = settings.passPolicy.refundAllowed,
                makeupEnabled = settings.makeupPolicy.enabled,
                makeupExpiresInDays = settings.makeupPolicy.expiresInDays,
                makeupMaxCount = settings.makeupPolicy.maxCount,
            )
    }
}

interface SpringDataAcademySettingsJpaRepository : JpaRepository<AcademySettingsJpaEntity, UUID>

@Repository
class JpaAcademySettingsRepository(
    private val springDataRepository: SpringDataAcademySettingsJpaRepository,
) : AcademySettingsRepository {
    override fun get(tenantId: UUID): AcademySettings =
        springDataRepository.findById(tenantId)
            .map { it.toDomain() }
            .orElseGet {
                save(
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
                    ),
                )
            }

    override fun save(settings: AcademySettings): AcademySettings =
        springDataRepository.save(AcademySettingsJpaEntity.fromDomain(settings)).toDomain()
}
