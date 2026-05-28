package com.dionomy.student.domain

import java.time.LocalDateTime
import java.util.UUID

class Student(
    val id: UUID,
    val tenantId: UUID,
    val name: String,
    val phone: String,
    val memo: String?,
    val tags: List<String> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    init {
        require(name.isNotBlank())
        require(phone.isNotBlank())
        require(tags.distinct().size == tags.size)
    }
}
