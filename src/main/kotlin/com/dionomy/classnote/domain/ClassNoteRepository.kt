package com.dionomy.classnote.domain

import java.util.UUID

interface ClassNoteRepository {
    fun save(note: ClassNote): ClassNote
    fun findByTenantAndSession(tenantId: UUID, sessionId: UUID): List<ClassNote>
    fun findByTenant(tenantId: UUID): List<ClassNote>
}
