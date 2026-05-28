package com.dionomy.adminsetup.domain

import java.util.UUID

interface TenantSetupRepository {
    fun save(setup: TenantSetup): TenantSetup
    fun findAll(): List<TenantSetup>
    fun findById(setupId: UUID): TenantSetup?
}
