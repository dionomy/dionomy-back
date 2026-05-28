package com.dionomy.pass.application

import com.dionomy.pass.domain.PassRepository
import com.dionomy.pass.domain.PassUsageLog
import com.dionomy.pass.domain.PassUsageType
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class RecordPassUsageUseCase(
    private val passRepository: PassRepository,
) {
    fun consume(command: RecordPassUsageCommand): PassUsageLog {
        val pass = requireNotNull(passRepository.findStudentPassByTenantAndId(command.tenantId, command.passId))
        pass.consume(command.count)
        passRepository.saveStudentPass(pass)
        return passRepository.appendUsageLog(command.toLog(pass.studentId, PassUsageType.CONSUME))
    }

    fun restore(command: RecordPassUsageCommand): PassUsageLog {
        val pass = requireNotNull(passRepository.findStudentPassByTenantAndId(command.tenantId, command.passId))
        pass.restore(command.count)
        passRepository.saveStudentPass(pass)
        return passRepository.appendUsageLog(command.toLog(pass.studentId, PassUsageType.RESTORE))
    }

    fun logs(tenantId: UUID, passId: UUID): List<PassUsageLog> =
        passRepository.findUsageLogsByTenantAndPass(tenantId, passId)
}

data class RecordPassUsageCommand(
    val tenantId: UUID,
    val passId: UUID,
    val count: Int,
    val reason: String,
) {
    fun toLog(studentId: UUID, type: PassUsageType): PassUsageLog =
        PassUsageLog(
            id = UUID.randomUUID(),
            tenantId = tenantId,
            passId = passId,
            studentId = studentId,
            type = type,
            count = count,
            reason = reason,
        )
}
