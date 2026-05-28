package com.dionomy.classnote.infrastructure

import com.dionomy.classnote.domain.ClassNote
import com.dionomy.classnote.domain.ClassNoteRepository
import org.springframework.stereotype.Repository
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Repository
class InMemoryClassNoteRepository : ClassNoteRepository {
    private val notes = ConcurrentHashMap<UUID, ClassNote>()

    override fun save(note: ClassNote): ClassNote {
        notes[note.id] = note
        return note
    }

    override fun findByTenantAndSession(tenantId: UUID, sessionId: UUID): List<ClassNote> =
        notes.values
            .filter { it.tenantId == tenantId && it.sessionId == sessionId }
            .sortedByDescending { it.createdAt }

    override fun findByTenant(tenantId: UUID): List<ClassNote> =
        notes.values
            .filter { it.tenantId == tenantId }
            .sortedByDescending { it.createdAt }
}
