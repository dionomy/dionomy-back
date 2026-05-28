package com.dionomy.adminsetup.application

import com.dionomy.adminsetup.domain.TenantSetup
import com.dionomy.adminsetup.domain.TenantSetupRepository
import com.dionomy.tenant.domain.TenantStatus
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UpdateTenantSetupStatusUseCase(
    private val tenantSetupRepository: TenantSetupRepository,
) {
    fun execute(setupId: UUID, status: TenantStatus): TenantSetup {
        val setup = tenantSetupRepository.findById(setupId)
            ?: throw NoSuchElementException("Tenant setup not found: $setupId")

        return tenantSetupRepository.save(
            TenantSetup(
                id = setup.id,
                academyName = setup.academyName,
                ownerContact = setup.ownerContact,
                mainColor = setup.mainColor,
                tenantStatus = status,
                buildStatus = setup.buildStatus,
                createdAt = setup.createdAt,
            ),
        )
    }
}
