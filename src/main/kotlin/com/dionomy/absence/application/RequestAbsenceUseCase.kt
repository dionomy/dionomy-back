package com.dionomy.absence.application

import com.dionomy.absence.domain.AbsenceDesiredResult
import com.dionomy.absence.domain.AbsenceRepository
import com.dionomy.absence.domain.AbsenceRequest
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class RequestAbsenceUseCase(
    private val absenceRepository: AbsenceRepository,
) {
    fun execute(command: RequestAbsenceCommand): AbsenceRequest =
        absenceRepository.save(
            AbsenceRequest(
                id = UUID.randomUUID(),
                tenantId = command.tenantId,
                studentId = command.studentId,
                sessionId = command.sessionId,
                reason = command.reason,
                desiredResult = command.desiredResult,
            ),
        )
}

data class RequestAbsenceCommand(
    val tenantId: UUID,
    val studentId: UUID,
    val sessionId: UUID,
    val reason: String,
    val desiredResult: AbsenceDesiredResult,
)
