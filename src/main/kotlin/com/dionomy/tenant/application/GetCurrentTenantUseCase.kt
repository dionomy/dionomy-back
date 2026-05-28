package com.dionomy.tenant.application

import com.dionomy.tenant.domain.Tenant
import com.dionomy.tenant.domain.TenantRepository
import com.dionomy.tenant.domain.TenantStatus
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetCurrentTenantUseCase(
    private val tenantRepository: TenantRepository,
) {
    fun execute(tenantId: UUID): Tenant =
        tenantRepository.get(tenantId)
}
