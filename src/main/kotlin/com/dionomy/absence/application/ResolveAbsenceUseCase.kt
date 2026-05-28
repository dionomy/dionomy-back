package com.dionomy.absence.application

import com.dionomy.absence.domain.AbsenceRepository
import com.dionomy.absence.domain.AbsenceRequest
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ResolveAbsenceUseCase(
    private val absenceRepository: AbsenceRepository,
) {
    fun approve(tenantId: UUID, requestId: UUID): AbsenceRequest {
        val request = requireNotNull(absenceRepository.findByTenantAndId(tenantId, requestId))
        request.approve()
        return absenceRepository.save(request)
    }

    fun reject(tenantId: UUID, requestId: UUID): AbsenceRequest {
        val request = requireNotNull(absenceRepository.findByTenantAndId(tenantId, requestId))
        request.reject()
        return absenceRepository.save(request)
    }
}
