package com.dionomy.adminsetup.infrastructure

import com.dionomy.adminsetup.domain.TenantSetup
import com.dionomy.adminsetup.domain.TenantSetupRepository
import com.dionomy.adminsetup.domain.WhiteLabelBuildStatus
import com.dionomy.tenant.domain.TenantStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "tenant_setups")
class TenantSetupJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "academy_name", nullable = false)
    var academyName: String = "",
    @Column(name = "owner_contact", nullable = false)
    var ownerContact: String = "",
    @Column(name = "main_color", nullable = false)
    var mainColor: String = "#4F46E5",
    @Enumerated(EnumType.STRING)
    @Column(name = "tenant_status", nullable = false)
    var tenantStatus: TenantStatus = TenantStatus.ACTIVE,
    @Enumerated(EnumType.STRING)
    @Column(name = "build_status", nullable = false)
    var buildStatus: WhiteLabelBuildStatus = WhiteLabelBuildStatus.QUEUED,
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
) {
    fun toDomain(): TenantSetup =
        TenantSetup(
            id = id,
            academyName = academyName,
            ownerContact = ownerContact,
            mainColor = mainColor,
            tenantStatus = tenantStatus,
            buildStatus = buildStatus,
            createdAt = createdAt,
        )

    companion object {
        fun fromDomain(setup: TenantSetup): TenantSetupJpaEntity =
            TenantSetupJpaEntity(
                id = setup.id,
                academyName = setup.academyName,
                ownerContact = setup.ownerContact,
                mainColor = setup.mainColor,
                tenantStatus = setup.tenantStatus,
                buildStatus = setup.buildStatus,
                createdAt = setup.createdAt,
            )
    }
}

interface SpringDataTenantSetupJpaRepository : JpaRepository<TenantSetupJpaEntity, UUID> {
    fun findAllByOrderByCreatedAtDesc(): List<TenantSetupJpaEntity>
}

@Repository
class JpaTenantSetupRepository(
    private val springDataRepository: SpringDataTenantSetupJpaRepository,
) : TenantSetupRepository {
    override fun save(setup: TenantSetup): TenantSetup =
        springDataRepository.save(TenantSetupJpaEntity.fromDomain(setup)).toDomain()

    override fun findAll(): List<TenantSetup> =
        springDataRepository.findAllByOrderByCreatedAtDesc().map { it.toDomain() }

    override fun findById(setupId: UUID): TenantSetup? =
        springDataRepository.findById(setupId).map { it.toDomain() }.orElse(null)
}
