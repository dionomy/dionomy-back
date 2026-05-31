package com.dionomy.tenant.infrastructure

import com.dionomy.tenant.domain.Tenant
import com.dionomy.tenant.domain.TenantRepository
import com.dionomy.tenant.domain.TenantStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Entity
@Table(name = "tenants")
class TenantJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "name", nullable = false)
    var name: String = "",
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: TenantStatus = TenantStatus.ACTIVE,
) {
    fun toDomain(): Tenant =
        Tenant(
            id = id,
            name = name,
            status = status,
        )

    companion object {
        fun fromDomain(tenant: Tenant): TenantJpaEntity =
            TenantJpaEntity(
                id = tenant.id,
                name = tenant.name,
                status = tenant.status,
            )
    }
}

interface SpringDataTenantJpaRepository : JpaRepository<TenantJpaEntity, UUID>

@Repository
class JpaTenantRepository(
    private val springDataRepository: SpringDataTenantJpaRepository,
) : TenantRepository {
    override fun get(tenantId: UUID): Tenant =
        springDataRepository.findById(tenantId)
            .map { it.toDomain() }
            .orElseGet {
                save(
                    Tenant(
                        id = tenantId,
                        name = "샘플 아카데미",
                        status = TenantStatus.ACTIVE,
                    ),
                )
            }

    override fun findAll(): List<Tenant> =
        springDataRepository.findAll().map { it.toDomain() }

    override fun save(tenant: Tenant): Tenant =
        springDataRepository.save(TenantJpaEntity.fromDomain(tenant)).toDomain()
}
