package com.dionomy.classnote.domain

import java.time.LocalDateTime
import java.util.UUID

class ClassNote(
    val id: UUID,
    val tenantId: UUID,
    val sessionId: UUID,
    val teacherId: UUID,
    val progress: String,
    val feedback: String,
    val nextAssignment: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    init {
        require(progress.isNotBlank())
        require(feedback.isNotBlank())
    }
}
