package com.dionomy.tenant.domain

import java.util.UUID

interface TenantRepository {
    fun get(tenantId: UUID): Tenant
    fun findByAcademyNumber(academyNumber: Int): Tenant?
    fun findAll(): List<Tenant>
    fun save(tenant: Tenant): Tenant
}
