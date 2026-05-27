package com.dionomy.tenant.application

import com.dionomy.tenant.domain.Tenant
import com.dionomy.tenant.domain.TenantStatus
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetCurrentTenantUseCase {
    fun execute(tenantId: UUID): Tenant =
        Tenant(
            id = tenantId,
            name = "샘플 아카데미",
            status = TenantStatus.ACTIVE,
        )
}
