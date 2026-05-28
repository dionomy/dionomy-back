package com.dionomy.company.presentation

import com.dionomy.company.application.CreateCsTicketCommand
import com.dionomy.company.application.CreateCsTicketUseCase
import com.dionomy.company.application.CreateDemoRequestCommand
import com.dionomy.company.application.CreateDemoRequestUseCase
import com.dionomy.company.application.ListCompanyIntakeUseCase
import com.dionomy.company.domain.CsTicket
import com.dionomy.company.domain.CsTicketStatus
import com.dionomy.company.domain.DemoRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.UUID

@RestController
@RequestMapping("/api/company")
class CompanyController(
    private val createDemoRequestUseCase: CreateDemoRequestUseCase,
    private val createCsTicketUseCase: CreateCsTicketUseCase,
    private val listCompanyIntakeUseCase: ListCompanyIntakeUseCase,
) {
    @GetMapping("/demo-requests")
    fun demoRequests(): List<DemoRequestResponse> =
        listCompanyIntakeUseCase.demoRequests().map { it.toResponse() }

    @PostMapping("/demo-requests")
    fun createDemoRequest(@Valid @RequestBody request: CreateDemoRequest): DemoRequestResponse =
        createDemoRequestUseCase.execute(request.toCommand()).toResponse()

    @GetMapping("/cs-tickets")
    fun csTickets(@RequestParam(required = false) contact: String?): List<CsTicketResponse> =
        listCompanyIntakeUseCase.csTickets(contact).map { it.toResponse() }

    @PostMapping("/cs-tickets")
    fun createCsTicket(@Valid @RequestBody request: CreateCsTicket): CsTicketResponse =
        createCsTicketUseCase.execute(request.toCommand()).toResponse()
}

data class CreateDemoRequest(
    @field:NotBlank
    val academyName: String,
    val businessType: String,
    val academySize: String,
    @field:NotBlank
    val contact: String,
) {
    fun toCommand(): CreateDemoRequestCommand =
        CreateDemoRequestCommand(
            academyName = academyName,
            businessType = businessType,
            academySize = academySize,
            contact = contact,
        )
}

data class CreateCsTicket(
    @field:NotBlank
    val title: String,
    @field:NotBlank
    val body: String,
    @field:NotBlank
    val contact: String,
) {
    fun toCommand(): CreateCsTicketCommand =
        CreateCsTicketCommand(title = title, body = body, contact = contact)
}

data class DemoRequestResponse(
    val id: UUID,
    val academyName: String,
    val businessType: String,
    val academySize: String,
    val contact: String,
    val createdAt: LocalDateTime,
)

data class CsTicketResponse(
    val id: UUID,
    val title: String,
    val body: String,
    val contact: String,
    val status: CsTicketStatus,
    val createdAt: LocalDateTime,
)

private fun DemoRequest.toResponse(): DemoRequestResponse =
    DemoRequestResponse(
        id = id,
        academyName = academyName,
        businessType = businessType,
        academySize = academySize,
        contact = contact,
        createdAt = createdAt,
    )

private fun CsTicket.toResponse(): CsTicketResponse =
    CsTicketResponse(
        id = id,
        title = title,
        body = body,
        contact = contact,
        status = status,
        createdAt = createdAt,
    )
