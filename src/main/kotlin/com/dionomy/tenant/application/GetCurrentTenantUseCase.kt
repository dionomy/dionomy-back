package com.dionomy.tenant.application

import com.dionomy.tenant.domain.Tenant
import com.dionomy.tenant.domain.TenantRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetCurrentTenantUseCase(
    private val tenantRepository: TenantRepository,
) {
    fun execute(tenantId: UUID): Tenant =
        tenantRepository.get(tenantId)

    fun byAcademyNumber(academyNumber: Int): Tenant =
        tenantRepository.findByAcademyNumber(academyNumber)
            ?: if (academyNumber == 1) {
                tenantRepository.get(DEFAULT_TENANT_ID)
            } else {
                null
            }
            ?: throw IllegalArgumentException(
                "Academy number not found: $academyNumber",
            )

    private companion object {
        val DEFAULT_TENANT_ID: UUID = UUID.fromString("00000000-0000-0000-0000-000000000001")
    }
}
