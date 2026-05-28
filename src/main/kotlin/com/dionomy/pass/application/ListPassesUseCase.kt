package com.dionomy.pass.application

import com.dionomy.pass.domain.PassProduct
import com.dionomy.pass.domain.PassRepository
import com.dionomy.pass.domain.StudentPass
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ListPassProductsUseCase(
    private val passRepository: PassRepository,
) {
    fun execute(tenantId: UUID): List<PassProduct> =
        passRepository.findProductsByTenant(tenantId)
}

@Service
class ListStudentPassesUseCase(
    private val passRepository: PassRepository,
) {
    fun execute(tenantId: UUID, studentId: UUID): List<StudentPass> =
        passRepository.findPassesByTenantAndStudent(tenantId, studentId)
}
