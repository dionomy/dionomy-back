package com.dionomy.company.domain

import java.util.UUID

interface CompanyRepository {
    fun saveDemoRequest(request: DemoRequest): DemoRequest
    fun findDemoRequests(): List<DemoRequest>
    fun saveCsTicket(ticket: CsTicket): CsTicket
    fun findCsTickets(contact: String?): List<CsTicket>
    fun findCsTicketById(ticketId: UUID): CsTicket?
}
