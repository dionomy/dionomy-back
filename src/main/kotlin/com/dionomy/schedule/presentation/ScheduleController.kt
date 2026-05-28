package com.dionomy.schedule.presentation

import com.dionomy.schedule.application.CreateClassSessionUseCase
import com.dionomy.schedule.application.ListClassSessionsUseCase
import com.dionomy.schedule.domain.ClassSession
import com.dionomy.schedule.domain.ClassType
import com.dionomy.schedule.domain.RecurrenceFrequency
import com.dionomy.schedule.domain.RecurrenceRule
import com.dionomy.schedule.domain.SessionCapacity
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@RestController
@RequestMapping("/api/schedules")
class ScheduleController(
    private val createClassSessionUseCase: CreateClassSessionUseCase,
    private val listClassSessionsUseCase: ListClassSessionsUseCase,
) {
    @GetMapping
    fun list(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) to: LocalDate,
    ): List<ClassSessionResponse> =
        listClassSessionsUseCase.execute(tenantId, from, to).map { it.toResponse() }

    @PostMapping
    fun create(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @Valid @RequestBody request: CreateClassSessionRequest,
    ): ClassSessionResponse =
        createClassSessionUseCase.execute(request.toDomain(tenantId)).toResponse()
}

data class CreateClassSessionRequest(
    @field:NotBlank
    val title: String,
    val type: ClassType,
    val teacherId: UUID,
    val placeId: UUID?,
    val startsAt: LocalDateTime,
    val endsAt: LocalDateTime,
    @field:PositiveOrZero
    val currentCapacity: Int,
    @field:Positive
    val maximumCapacity: Int,
    val assignedStudentIds: List<UUID> = emptyList(),
    val recurrence: RecurrenceRequest? = null,
) {
    fun toDomain(tenantId: UUID): ClassSession =
        ClassSession(
            id = UUID.randomUUID(),
            tenantId = tenantId,
            title = title,
            type = type,
            teacherId = teacherId,
            placeId = placeId,
            startsAt = startsAt,
            endsAt = endsAt,
            capacity = SessionCapacity(
                current = currentCapacity,
                maximum = maximumCapacity,
            ),
            assignedStudentIds = assignedStudentIds,
            recurrence = recurrence?.toDomain(),
        )
}

data class RecurrenceRequest(
    val frequency: RecurrenceFrequency,
    val daysOfWeek: Set<DayOfWeek>,
    val until: LocalDate,
) {
    fun toDomain(): RecurrenceRule =
        RecurrenceRule(
            frequency = frequency,
            daysOfWeek = daysOfWeek,
            until = until,
        )
}

data class ClassSessionResponse(
    val id: UUID,
    val tenantId: UUID,
    val title: String,
    val type: ClassType,
    val teacherId: UUID,
    val placeId: UUID?,
    val startsAt: LocalDateTime,
    val endsAt: LocalDateTime,
    val currentCapacity: Int,
    val maximumCapacity: Int,
    val assignedStudentIds: List<UUID>,
    val recurrence: RecurrenceResponse?,
)

data class RecurrenceResponse(
    val frequency: RecurrenceFrequency,
    val daysOfWeek: Set<DayOfWeek>,
    val until: LocalDate,
)

private fun ClassSession.toResponse(): ClassSessionResponse =
    ClassSessionResponse(
        id = id,
        tenantId = tenantId,
        title = title,
        type = type,
        teacherId = teacherId,
        placeId = placeId,
        startsAt = startsAt,
        endsAt = endsAt,
        currentCapacity = capacity.current,
        maximumCapacity = capacity.maximum,
        assignedStudentIds = assignedStudentIds,
        recurrence = recurrence?.let {
            RecurrenceResponse(
                frequency = it.frequency,
                daysOfWeek = it.daysOfWeek,
                until = it.until,
            )
        },
    )
