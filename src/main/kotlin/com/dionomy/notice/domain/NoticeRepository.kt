package com.dionomy.notice.domain

import java.util.UUID

interface NoticeRepository {
    fun save(notice: Notice): Notice
    fun findByTenant(tenantId: UUID): List<Notice>
}
