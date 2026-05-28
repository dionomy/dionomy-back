package com.dionomy.company.infrastructure

import com.dionomy.company.domain.CompanyRepository
import com.dionomy.company.domain.CsTicket
import com.dionomy.company.domain.CsTicketStatus
import com.dionomy.company.domain.DemoRequest
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "demo_requests")
class DemoRequestJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "academy_name", nullable = false)
    var academyName: String = "",
    @Column(name = "business_type", nullable = false)
    var businessType: String = "",
    @Column(name = "academy_size", nullable = false)
    var academySize: String = "",
    @Column(name = "contact", nullable = false)
    var contact: String = "",
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
) {
    fun toDomain(): DemoRequest =
        DemoRequest(
            id = id,
            academyName = academyName,
            businessType = businessType,
            academySize = academySize,
            contact = contact,
            createdAt = createdAt,
        )

    companion object {
        fun fromDomain(request: DemoRequest): DemoRequestJpaEntity =
            DemoRequestJpaEntity(
                id = request.id,
                academyName = request.academyName,
                businessType = request.businessType,
                academySize = request.academySize,
                contact = request.contact,
                createdAt = request.createdAt,
            )
    }
}

@Entity
@Table(name = "cs_tickets")
class CsTicketJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "title", nullable = false)
    var title: String = "",
    @Column(name = "body", nullable = false)
    var body: String = "",
    @Column(name = "contact", nullable = false)
    var contact: String = "",
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: CsTicketStatus = CsTicketStatus.OPEN,
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
) {
    fun toDomain(): CsTicket =
        CsTicket(
            id = id,
            title = title,
            body = body,
            contact = contact,
            status = status,
            createdAt = createdAt,
        )

    companion object {
        fun fromDomain(ticket: CsTicket): CsTicketJpaEntity =
            CsTicketJpaEntity(
                id = ticket.id,
                title = ticket.title,
                body = ticket.body,
                contact = ticket.contact,
                status = ticket.status,
                createdAt = ticket.createdAt,
            )
    }
}

interface SpringDataDemoRequestJpaRepository : JpaRepository<DemoRequestJpaEntity, UUID> {
    fun findAllByOrderByCreatedAtDesc(): List<DemoRequestJpaEntity>
}

interface SpringDataCsTicketJpaRepository : JpaRepository<CsTicketJpaEntity, UUID> {
    fun findAllByOrderByCreatedAtDesc(): List<CsTicketJpaEntity>
    fun findByContactOrderByCreatedAtDesc(contact: String): List<CsTicketJpaEntity>
}

@Repository
class JpaCompanyRepository(
    private val demoRequestRepository: SpringDataDemoRequestJpaRepository,
    private val csTicketRepository: SpringDataCsTicketJpaRepository,
) : CompanyRepository {
    override fun saveDemoRequest(request: DemoRequest): DemoRequest =
        demoRequestRepository.save(DemoRequestJpaEntity.fromDomain(request)).toDomain()

    override fun findDemoRequests(): List<DemoRequest> =
        demoRequestRepository.findAllByOrderByCreatedAtDesc().map { it.toDomain() }

    override fun saveCsTicket(ticket: CsTicket): CsTicket =
        csTicketRepository.save(CsTicketJpaEntity.fromDomain(ticket)).toDomain()

    override fun findCsTickets(contact: String?): List<CsTicket> =
        if (contact == null) {
            csTicketRepository.findAllByOrderByCreatedAtDesc()
        } else {
            csTicketRepository.findByContactOrderByCreatedAtDesc(contact)
        }.map { it.toDomain() }

    override fun findCsTicketById(ticketId: UUID): CsTicket? =
        csTicketRepository.findById(ticketId).map { it.toDomain() }.orElse(null)
}
