package com.dionomy.operation.presentation

import com.dionomy.operation.application.CreateInstructorUseCase
import com.dionomy.operation.application.CreateInstructorAvailabilityCommand
import com.dionomy.operation.application.CreateInstructorAvailabilityUseCase
import com.dionomy.operation.application.CreatePlaceUseCase
import com.dionomy.operation.application.ListInstructorAvailabilitiesUseCase
import com.dionomy.operation.application.ListInstructorsUseCase
import com.dionomy.operation.application.ListPlacesUseCase
import com.dionomy.operation.domain.Instructor
import com.dionomy.operation.domain.InstructorAvailability
import com.dionomy.operation.domain.Place
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.format.annotation.DateTimeFormat
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
@RequestMapping("/api/operation")
class OperationResourceController(
    private val listInstructorsUseCase: ListInstructorsUseCase,
    private val createInstructorUseCase: CreateInstructorUseCase,
    private val listInstructorAvailabilitiesUseCase: ListInstructorAvailabilitiesUseCase,
    private val createInstructorAvailabilityUseCase: CreateInstructorAvailabilityUseCase,
    private val listPlacesUseCase: ListPlacesUseCase,
    private val createPlaceUseCase: CreatePlaceUseCase,
) {
    @GetMapping("/instructors")
    fun listInstructors(@RequestHeader("X-Tenant-Id") tenantId: UUID): List<InstructorResponse> =
        listInstructorsUseCase.execute(tenantId).map { it.toResponse() }

    @PostMapping("/instructors")
    fun createInstructor(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @Valid @RequestBody request: CreateInstructorRequest,
    ): InstructorResponse =
        createInstructorUseCase.execute(tenantId, request.name, request.phone).toResponse()

    @GetMapping("/instructor-availabilities")
    fun listInstructorAvailabilities(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @RequestParam(required = false) instructorId: UUID?,
    ): List<InstructorAvailabilityResponse> =
        listInstructorAvailabilitiesUseCase.execute(tenantId, instructorId).map { it.toResponse() }

    @PostMapping("/instructor-availabilities")
    fun createInstructorAvailability(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @Valid @RequestBody request: CreateInstructorAvailabilityRequest,
    ): InstructorAvailabilityResponse =
        createInstructorAvailabilityUseCase.execute(request.toCommand(tenantId)).toResponse()

    @GetMapping("/places")
    fun listPlaces(@RequestHeader("X-Tenant-Id") tenantId: UUID): List<PlaceResponse> =
        listPlacesUseCase.execute(tenantId).map { it.toResponse() }

    @PostMapping("/places")
    fun createPlace(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @Valid @RequestBody request: CreatePlaceRequest,
    ): PlaceResponse =
        createPlaceUseCase.execute(tenantId, request.name, request.memo).toResponse()
}

data class CreateInstructorRequest(
    @field:NotBlank
    val name: String,
    val phone: String?,
)

data class CreatePlaceRequest(
    @field:NotBlank
    val name: String,
    val memo: String?,
)

data class CreateInstructorAvailabilityRequest(
    val instructorId: UUID,
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val startsAt: LocalDateTime,
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val endsAt: LocalDateTime,
    val memo: String?,
) {
    fun toCommand(tenantId: UUID): CreateInstructorAvailabilityCommand =
        CreateInstructorAvailabilityCommand(
            tenantId = tenantId,
            instructorId = instructorId,
            startsAt = startsAt,
            endsAt = endsAt,
            memo = memo,
        )
}

data class InstructorResponse(
    val id: UUID,
    val tenantId: UUID,
    val name: String,
    val phone: String?,
)

data class PlaceResponse(
    val id: UUID,
    val tenantId: UUID,
    val name: String,
    val memo: String?,
)

data class InstructorAvailabilityResponse(
    val id: UUID,
    val tenantId: UUID,
    val instructorId: UUID,
    val startsAt: LocalDateTime,
    val endsAt: LocalDateTime,
    val memo: String?,
)

private fun Instructor.toResponse(): InstructorResponse =
    InstructorResponse(
        id = id,
        tenantId = tenantId,
        name = name,
        phone = phone,
    )

private fun Place.toResponse(): PlaceResponse =
    PlaceResponse(
        id = id,
        tenantId = tenantId,
        name = name,
        memo = memo,
    )

private fun InstructorAvailability.toResponse(): InstructorAvailabilityResponse =
    InstructorAvailabilityResponse(
        id = id,
        tenantId = tenantId,
        instructorId = instructorId,
        startsAt = startsAt,
        endsAt = endsAt,
        memo = memo,
    )
