package com.dionomy.notice.infrastructure

import com.dionomy.notice.domain.Notice
import com.dionomy.notice.domain.NoticeRepository
import org.springframework.stereotype.Repository
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Repository
class InMemoryNoticeRepository : NoticeRepository {
    private val notices = ConcurrentHashMap<UUID, Notice>()

    override fun save(notice: Notice): Notice {
        notices[notice.id] = notice
        return notice
    }

    override fun findByTenant(tenantId: UUID): List<Notice> =
        notices.values
            .filter { it.tenantId == tenantId }
            .sortedByDescending { it.createdAt }
}
