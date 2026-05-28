package com.dionomy.notice.application

import com.dionomy.notice.domain.Notice
import com.dionomy.notice.domain.NoticeRepository
import com.dionomy.notice.domain.NoticeTarget
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CreateNoticeUseCase(
    private val noticeRepository: NoticeRepository,
) {
    fun execute(command: CreateNoticeCommand): Notice =
        noticeRepository.save(
            Notice(
                id = UUID.randomUUID(),
                tenantId = command.tenantId,
                title = command.title,
                body = command.body,
                imageUrl = command.imageUrl,
                target = command.target,
                classId = command.classId,
            ),
        )
}

data class CreateNoticeCommand(
    val tenantId: UUID,
    val title: String,
    val body: String,
    val imageUrl: String?,
    val target: NoticeTarget,
    val classId: UUID?,
)
