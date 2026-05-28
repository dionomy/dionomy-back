package com.dionomy.company.infrastructure

import com.dionomy.company.domain.CompanyRepository
import com.dionomy.company.domain.CsTicket
import com.dionomy.company.domain.DemoRequest
import org.springframework.stereotype.Repository
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Repository
class InMemoryCompanyRepository : CompanyRepository {
    private val demoRequests = ConcurrentHashMap<UUID, DemoRequest>()
    private val csTickets = ConcurrentHashMap<UUID, CsTicket>()

    override fun saveDemoRequest(request: DemoRequest): DemoRequest {
        demoRequests[request.id] = request
        return request
    }

    override fun findDemoRequests(): List<DemoRequest> =
        demoRequests.values.sortedByDescending { it.createdAt }

    override fun saveCsTicket(ticket: CsTicket): CsTicket {
        csTickets[ticket.id] = ticket
        return ticket
    }

    override fun findCsTickets(contact: String?): List<CsTicket> =
        csTickets.values
            .filter { contact == null || it.contact == contact }
            .sortedByDescending { it.createdAt }

    override fun findCsTicketById(ticketId: UUID): CsTicket? =
        csTickets[ticketId]
}
