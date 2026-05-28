package com.dionomy.classnote.application

import com.dionomy.classnote.domain.ClassNote
import com.dionomy.classnote.domain.ClassNoteRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CreateClassNoteUseCase(
    private val classNoteRepository: ClassNoteRepository,
) {
    fun execute(command: CreateClassNoteCommand): ClassNote =
        classNoteRepository.save(
            ClassNote(
                id = UUID.randomUUID(),
                tenantId = command.tenantId,
                sessionId = command.sessionId,
                teacherId = command.teacherId,
                progress = command.progress,
                feedback = command.feedback,
                nextAssignment = command.nextAssignment,
            ),
        )
}

data class CreateClassNoteCommand(
    val tenantId: UUID,
    val sessionId: UUID,
    val teacherId: UUID,
    val progress: String,
    val feedback: String,
    val nextAssignment: String,
)
