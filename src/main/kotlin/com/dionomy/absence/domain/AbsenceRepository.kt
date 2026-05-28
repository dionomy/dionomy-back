package com.dionomy.absence.domain

import java.util.UUID

interface AbsenceRepository {
    fun save(request: AbsenceRequest): AbsenceRequest
    fun findByTenant(tenantId: UUID): List<AbsenceRequest>
    fun findByTenantAndStudent(tenantId: UUID, studentId: UUID): List<AbsenceRequest>
    fun findByTenantAndId(tenantId: UUID, requestId: UUID): AbsenceRequest?
}
