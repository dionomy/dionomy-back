package com.dionomy.absence.application

import com.dionomy.absence.domain.AbsenceRepository
import com.dionomy.absence.domain.AbsenceRequest
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ListAbsenceRequestsUseCase(
    private val absenceRepository: AbsenceRepository,
) {
    fun byTenant(tenantId: UUID): List<AbsenceRequest> =
        absenceRepository.findByTenant(tenantId)

    fun byStudent(tenantId: UUID, studentId: UUID): List<AbsenceRequest> =
        absenceRepository.findByTenantAndStudent(tenantId, studentId)
}
