package com.dionomy.notice.application

import com.dionomy.notice.domain.Notice
import com.dionomy.notice.domain.NoticeRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ListNoticesUseCase(
    private val noticeRepository: NoticeRepository,
) {
    fun execute(tenantId: UUID): List<Notice> =
        noticeRepository.findByTenant(tenantId)
}
