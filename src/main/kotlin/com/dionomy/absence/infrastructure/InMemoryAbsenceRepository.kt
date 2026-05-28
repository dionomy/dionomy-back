package com.dionomy.absence.infrastructure

import com.dionomy.absence.domain.AbsenceRepository
import com.dionomy.absence.domain.AbsenceRequest
import org.springframework.stereotype.Repository
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Repository
class InMemoryAbsenceRepository : AbsenceRepository {
    private val requests = ConcurrentHashMap<UUID, AbsenceRequest>()

    override fun save(request: AbsenceRequest): AbsenceRequest {
        requests[request.id] = request
        return request
    }

    override fun findByTenant(tenantId: UUID): List<AbsenceRequest> =
        requests.values
            .filter { it.tenantId == tenantId }
            .sortedByDescending { it.requestedAt }

    override fun findByTenantAndStudent(tenantId: UUID, studentId: UUID): List<AbsenceRequest> =
        requests.values
            .filter { it.tenantId == tenantId && it.studentId == studentId }
            .sortedByDescending { it.requestedAt }

    override fun findByTenantAndId(tenantId: UUID, requestId: UUID): AbsenceRequest? =
        requests[requestId]?.takeIf { it.tenantId == tenantId }
}
