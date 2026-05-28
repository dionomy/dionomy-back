package com.dionomy.adminsetup.application

import com.dionomy.adminsetup.domain.TenantSetup
import com.dionomy.adminsetup.domain.TenantSetupRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CreateTenantSetupUseCase(
    private val tenantSetupRepository: TenantSetupRepository,
) {
    fun execute(command: CreateTenantSetupCommand): TenantSetup =
        tenantSetupRepository.save(
            TenantSetup(
                id = UUID.randomUUID(),
                academyName = command.academyName,
                ownerContact = command.ownerContact,
                mainColor = command.mainColor,
            ),
        )
}

@Service
class ListTenantSetupsUseCase(
    private val tenantSetupRepository: TenantSetupRepository,
) {
    fun execute(): List<TenantSetup> =
        tenantSetupRepository.findAll()
}

data class CreateTenantSetupCommand(
    val academyName: String,
    val ownerContact: String,
    val mainColor: String,
)
