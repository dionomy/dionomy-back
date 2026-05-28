package com.dionomy.notice.domain

import java.time.LocalDateTime
import java.util.UUID

class Notice(
    val id: UUID,
    val tenantId: UUID,
    val title: String,
    val body: String,
    val imageUrl: String?,
    val target: NoticeTarget,
    val classId: UUID?,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    init {
        require(title.isNotBlank())
        require(body.isNotBlank())
        require(target == NoticeTarget.ALL || classId != null)
    }
}
