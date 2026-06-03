package com.dionomy.academy.infrastructure

import com.dionomy.academy.domain.AcademySettings
import com.dionomy.academy.domain.AcademySettingsRepository
import com.dionomy.academy.domain.Branding
import com.dionomy.academy.domain.FeatureSettings
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
    @Column(name = "owner_schedule_enabled", nullable = false)
    var ownerScheduleEnabled: Boolean = true,
    @Column(name = "owner_students_enabled", nullable = false)
    var ownerStudentsEnabled: Boolean = true,
    @Column(name = "owner_notices_enabled", nullable = false)
    var ownerNoticesEnabled: Boolean = true,
    @Column(name = "teacher_mode_enabled", nullable = false)
    var teacherModeEnabled: Boolean = true,
    @Column(name = "student_pass_enabled", nullable = false)
    var studentPassEnabled: Boolean = true,
    @Column(name = "student_class_notes_enabled", nullable = false)
    var studentClassNotesEnabled: Boolean = true,
    @Column(name = "student_absence_request_enabled", nullable = false)
    var studentAbsenceRequestEnabled: Boolean = true,
    @Column(name = "crm_enabled", nullable = false)
    var crmEnabled: Boolean = true,
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
            featureSettings = FeatureSettings(
                ownerScheduleEnabled = ownerScheduleEnabled,
                ownerStudentsEnabled = ownerStudentsEnabled,
                ownerNoticesEnabled = ownerNoticesEnabled,
                teacherModeEnabled = teacherModeEnabled,
                studentPassEnabled = studentPassEnabled,
                studentClassNotesEnabled = studentClassNotesEnabled,
                studentAbsenceRequestEnabled = studentAbsenceRequestEnabled,
                crmEnabled = crmEnabled,
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
                ownerScheduleEnabled = settings.featureSettings.ownerScheduleEnabled,
                ownerStudentsEnabled = settings.featureSettings.ownerStudentsEnabled,
                ownerNoticesEnabled = settings.featureSettings.ownerNoticesEnabled,
                teacherModeEnabled = settings.featureSettings.teacherModeEnabled,
                studentPassEnabled = settings.featureSettings.studentPassEnabled,
                studentClassNotesEnabled = settings.featureSettings.studentClassNotesEnabled,
                studentAbsenceRequestEnabled = settings.featureSettings.studentAbsenceRequestEnabled,
                crmEnabled = settings.featureSettings.crmEnabled,
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
                        featureSettings = FeatureSettings(
                            ownerScheduleEnabled = true,
                            ownerStudentsEnabled = true,
                            ownerNoticesEnabled = true,
                            teacherModeEnabled = true,
                            studentPassEnabled = true,
                            studentClassNotesEnabled = true,
                            studentAbsenceRequestEnabled = true,
                            crmEnabled = true,
                        ),
                    ),
                )
            }

    override fun save(settings: AcademySettings): AcademySettings =
        springDataRepository.save(AcademySettingsJpaEntity.fromDomain(settings)).toDomain()
}
