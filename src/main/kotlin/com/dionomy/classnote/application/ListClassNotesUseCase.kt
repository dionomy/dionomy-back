package com.dionomy.classnote.application

import com.dionomy.classnote.domain.ClassNote
import com.dionomy.classnote.domain.ClassNoteRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ListClassNotesUseCase(
    private val classNoteRepository: ClassNoteRepository,
) {
    fun byTenant(tenantId: UUID): List<ClassNote> =
        classNoteRepository.findByTenant(tenantId)

    fun bySession(tenantId: UUID, sessionId: UUID): List<ClassNote> =
        classNoteRepository.findByTenantAndSession(tenantId, sessionId)
}
