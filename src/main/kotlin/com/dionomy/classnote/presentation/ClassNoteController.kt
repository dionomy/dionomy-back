package com.dionomy.classnote.presentation

import com.dionomy.classnote.application.CreateClassNoteCommand
import com.dionomy.classnote.application.CreateClassNoteUseCase
import com.dionomy.classnote.application.ListClassNotesUseCase
import com.dionomy.classnote.domain.ClassNote
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.UUID

@RestController
@RequestMapping("/api/class-notes")
class ClassNoteController(
    private val createClassNoteUseCase: CreateClassNoteUseCase,
    private val listClassNotesUseCase: ListClassNotesUseCase,
) {
    @GetMapping
    fun list(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @RequestParam(required = false) sessionId: UUID?,
    ): List<ClassNoteResponse> =
        if (sessionId == null) {
            listClassNotesUseCase.byTenant(tenantId)
        } else {
            listClassNotesUseCase.bySession(tenantId, sessionId)
        }.map { it.toResponse() }

    @PostMapping
    fun create(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @Valid @RequestBody request: CreateClassNoteRequest,
    ): ClassNoteResponse =
        createClassNoteUseCase.execute(request.toCommand(tenantId)).toResponse()
}

data class CreateClassNoteRequest(
    val sessionId: UUID,
    val teacherId: UUID,
    @field:NotBlank
    val progress: String,
    @field:NotBlank
    val feedback: String,
    val nextAssignment: String,
) {
    fun toCommand(tenantId: UUID): CreateClassNoteCommand =
        CreateClassNoteCommand(
            tenantId = tenantId,
            sessionId = sessionId,
            teacherId = teacherId,
            progress = progress,
            feedback = feedback,
            nextAssignment = nextAssignment,
        )
}

data class ClassNoteResponse(
    val id: UUID,
    val sessionId: UUID,
    val teacherId: UUID,
    val progress: String,
    val feedback: String,
    val nextAssignment: String,
    val createdAt: LocalDateTime,
)

private fun ClassNote.toResponse(): ClassNoteResponse =
    ClassNoteResponse(
        id = id,
        sessionId = sessionId,
        teacherId = teacherId,
        progress = progress,
        feedback = feedback,
        nextAssignment = nextAssignment,
        createdAt = createdAt,
    )
