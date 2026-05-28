package com.dionomy.pass.application

import com.dionomy.pass.domain.PassProduct
import com.dionomy.pass.domain.PassRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CreatePassProductUseCase(
    private val passRepository: PassRepository,
) {
    fun execute(command: CreatePassProductCommand): PassProduct =
        passRepository.saveProduct(
            PassProduct(
                id = UUID.randomUUID(),
                tenantId = command.tenantId,
                name = command.name,
                totalCount = command.totalCount,
                validDays = command.validDays,
                price = command.price,
            ),
        )
}

data class CreatePassProductCommand(
    val tenantId: UUID,
    val name: String,
    val totalCount: Int,
    val validDays: Int,
    val price: Long,
)
