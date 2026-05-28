package com.dionomy.pass.application

import com.dionomy.pass.domain.PassRepository
import com.dionomy.pass.domain.StudentPass
import com.dionomy.student.domain.StudentRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.UUID

@Service
class IssueStudentPassUseCase(
    private val passRepository: PassRepository,
    private val studentRepository: StudentRepository,
) {
    fun execute(command: IssueStudentPassCommand): StudentPass {
        require(studentRepository.findByTenantAndId(command.tenantId, command.studentId) != null)
        val product = requireNotNull(passRepository.findProductByTenantAndId(command.tenantId, command.productId))
        val issuedOn = command.issuedOn ?: LocalDate.now()

        return passRepository.saveStudentPass(
            StudentPass(
                id = UUID.randomUUID(),
                tenantId = command.tenantId,
                productId = product.id,
                studentId = command.studentId,
                totalCount = product.totalCount,
                usedCountValue = 0,
                issuedOn = issuedOn,
                expiresOn = issuedOn.plusDays(product.validDays.toLong()),
            ),
        )
    }
}

data class IssueStudentPassCommand(
    val tenantId: UUID,
    val studentId: UUID,
    val productId: UUID,
    val issuedOn: LocalDate?,
)
