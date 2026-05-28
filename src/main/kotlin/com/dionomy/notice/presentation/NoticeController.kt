package com.dionomy.notice.presentation

import com.dionomy.notice.application.CreateNoticeCommand
import com.dionomy.notice.application.CreateNoticeUseCase
import com.dionomy.notice.application.ListNoticesUseCase
import com.dionomy.notice.domain.Notice
import com.dionomy.notice.domain.NoticeTarget
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.UUID

@RestController
@RequestMapping("/api/notices")
class NoticeController(
    private val createNoticeUseCase: CreateNoticeUseCase,
    private val listNoticesUseCase: ListNoticesUseCase,
) {
    @GetMapping
    fun list(@RequestHeader("X-Tenant-Id") tenantId: UUID): List<NoticeResponse> =
        listNoticesUseCase.execute(tenantId).map { it.toResponse() }

    @PostMapping
    fun create(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @Valid @RequestBody request: CreateNoticeRequest,
    ): NoticeResponse =
        createNoticeUseCase.execute(request.toCommand(tenantId)).toResponse()
}

data class CreateNoticeRequest(
    @field:NotBlank
    val title: String,
    @field:NotBlank
    val body: String,
    val imageUrl: String?,
    val target: NoticeTarget,
    val classId: UUID?,
) {
    fun toCommand(tenantId: UUID): CreateNoticeCommand =
        CreateNoticeCommand(
            tenantId = tenantId,
            title = title,
            body = body,
            imageUrl = imageUrl,
            target = target,
            classId = classId,
        )
}

data class NoticeResponse(
    val id: UUID,
    val title: String,
    val body: String,
    val imageUrl: String?,
    val target: NoticeTarget,
    val classId: UUID?,
    val createdAt: LocalDateTime,
)

private fun Notice.toResponse(): NoticeResponse =
    NoticeResponse(
        id = id,
        title = title,
        body = body,
        imageUrl = imageUrl,
        target = target,
        classId = classId,
        createdAt = createdAt,
    )
