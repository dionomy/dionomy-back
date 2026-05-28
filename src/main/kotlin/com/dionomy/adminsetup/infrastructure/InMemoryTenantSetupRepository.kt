package com.dionomy.adminsetup.infrastructure

import com.dionomy.adminsetup.domain.TenantSetup
import com.dionomy.adminsetup.domain.TenantSetupRepository
import org.springframework.stereotype.Repository
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Repository
class InMemoryTenantSetupRepository : TenantSetupRepository {
    private val setups = ConcurrentHashMap<UUID, TenantSetup>()

    override fun save(setup: TenantSetup): TenantSetup {
        setups[setup.id] = setup
        return setup
    }

    override fun findAll(): List<TenantSetup> =
        setups.values.sortedByDescending { it.createdAt }

    override fun findById(setupId: UUID): TenantSetup? =
        setups[setupId]
}
