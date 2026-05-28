package com.dionomy.company.application

import com.dionomy.company.domain.CompanyRepository
import com.dionomy.company.domain.CsTicket
import com.dionomy.company.domain.DemoRequest
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CreateDemoRequestUseCase(
    private val companyRepository: CompanyRepository,
) {
    fun execute(command: CreateDemoRequestCommand): DemoRequest =
        companyRepository.saveDemoRequest(
            DemoRequest(
                id = UUID.randomUUID(),
                academyName = command.academyName,
                businessType = command.businessType,
                academySize = command.academySize,
                contact = command.contact,
            ),
        )
}

@Service
class CreateCsTicketUseCase(
    private val companyRepository: CompanyRepository,
) {
    fun execute(command: CreateCsTicketCommand): CsTicket =
        companyRepository.saveCsTicket(
            CsTicket(
                id = UUID.randomUUID(),
                title = command.title,
                body = command.body,
                contact = command.contact,
            ),
        )
}

@Service
class ListCompanyIntakeUseCase(
    private val companyRepository: CompanyRepository,
) {
    fun demoRequests(): List<DemoRequest> =
        companyRepository.findDemoRequests()

    fun csTickets(contact: String?): List<CsTicket> =
        companyRepository.findCsTickets(contact)
}

data class CreateDemoRequestCommand(
    val academyName: String,
    val businessType: String,
    val academySize: String,
    val contact: String,
)

data class CreateCsTicketCommand(
    val title: String,
    val body: String,
    val contact: String,
)
