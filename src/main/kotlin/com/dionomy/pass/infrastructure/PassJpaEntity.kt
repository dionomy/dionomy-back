package com.dionomy.pass.infrastructure

import com.dionomy.pass.domain.PassProduct
import com.dionomy.pass.domain.PassRepository
import com.dionomy.pass.domain.PassUsageLog
import com.dionomy.pass.domain.PassUsageType
import com.dionomy.pass.domain.StudentPass
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "pass_products")
class PassProductJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "tenant_id", nullable = false)
    var tenantId: UUID = UUID.randomUUID(),
    @Column(name = "name", nullable = false)
    var name: String = "",
    @Column(name = "total_count", nullable = false)
    var totalCount: Int = 1,
    @Column(name = "valid_days", nullable = false)
    var validDays: Int = 1,
    @Column(name = "price", nullable = false)
    var price: Long = 0,
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
) {
    fun toDomain(): PassProduct =
        PassProduct(
            id = id,
            tenantId = tenantId,
            name = name,
            totalCount = totalCount,
            validDays = validDays,
            price = price,
            createdAt = createdAt,
        )

    companion object {
        fun fromDomain(product: PassProduct): PassProductJpaEntity =
            PassProductJpaEntity(
                id = product.id,
                tenantId = product.tenantId,
                name = product.name,
                totalCount = product.totalCount,
                validDays = product.validDays,
                price = product.price,
                createdAt = product.createdAt,
            )
    }
}

@Entity
@Table(name = "student_passes")
class StudentPassJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "tenant_id", nullable = false)
    var tenantId: UUID = UUID.randomUUID(),
    @Column(name = "product_id", nullable = false)
    var productId: UUID = UUID.randomUUID(),
    @Column(name = "student_id", nullable = false)
    var studentId: UUID = UUID.randomUUID(),
    @Column(name = "total_count", nullable = false)
    var totalCount: Int = 1,
    @Column(name = "used_count", nullable = false)
    var usedCount: Int = 0,
    @Column(name = "issued_on", nullable = false)
    var issuedOn: LocalDate = LocalDate.now(),
    @Column(name = "expires_on", nullable = false)
    var expiresOn: LocalDate = LocalDate.now(),
) {
    fun toDomain(): StudentPass =
        StudentPass(
            id = id,
            tenantId = tenantId,
            productId = productId,
            studentId = studentId,
            totalCount = totalCount,
            usedCountValue = usedCount,
            issuedOn = issuedOn,
            expiresOn = expiresOn,
        )

    companion object {
        fun fromDomain(pass: StudentPass): StudentPassJpaEntity =
            StudentPassJpaEntity(
                id = pass.id,
                tenantId = pass.tenantId,
                productId = pass.productId,
                studentId = pass.studentId,
                totalCount = pass.totalCount,
                usedCount = pass.usedCount,
                issuedOn = pass.issuedOn,
                expiresOn = pass.expiresOn,
            )
    }
}

@Entity
@Table(name = "pass_usage_logs")
class PassUsageLogJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "tenant_id", nullable = false)
    var tenantId: UUID = UUID.randomUUID(),
    @Column(name = "pass_id", nullable = false)
    var passId: UUID = UUID.randomUUID(),
    @Column(name = "student_id", nullable = false)
    var studentId: UUID = UUID.randomUUID(),
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: PassUsageType = PassUsageType.CONSUME,
    @Column(name = "count", nullable = false)
    var count: Int = 1,
    @Column(name = "reason", nullable = false)
    var reason: String = "",
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
) {
    fun toDomain(): PassUsageLog =
        PassUsageLog(
            id = id,
            tenantId = tenantId,
            passId = passId,
            studentId = studentId,
            type = type,
            count = count,
            reason = reason,
            createdAt = createdAt,
        )

    companion object {
        fun fromDomain(log: PassUsageLog): PassUsageLogJpaEntity =
            PassUsageLogJpaEntity(
                id = log.id,
                tenantId = log.tenantId,
                passId = log.passId,
                studentId = log.studentId,
                type = log.type,
                count = log.count,
                reason = log.reason,
                createdAt = log.createdAt,
            )
    }
}

interface SpringDataPassProductJpaRepository : JpaRepository<PassProductJpaEntity, UUID> {
    fun findByTenantIdOrderByNameAsc(tenantId: UUID): List<PassProductJpaEntity>
    fun findByTenantIdAndId(tenantId: UUID, id: UUID): PassProductJpaEntity?
}

interface SpringDataStudentPassJpaRepository : JpaRepository<StudentPassJpaEntity, UUID> {
    fun findByTenantIdOrderByExpiresOnDesc(tenantId: UUID): List<StudentPassJpaEntity>
    fun findByTenantIdAndStudentIdOrderByExpiresOnDesc(tenantId: UUID, studentId: UUID): List<StudentPassJpaEntity>
    fun findByTenantIdAndId(tenantId: UUID, id: UUID): StudentPassJpaEntity?
}

interface SpringDataPassUsageLogJpaRepository : JpaRepository<PassUsageLogJpaEntity, UUID> {
    fun findByTenantIdAndPassIdOrderByCreatedAtDesc(tenantId: UUID, passId: UUID): List<PassUsageLogJpaEntity>
}

@Repository
class JpaPassRepository(
    private val productRepository: SpringDataPassProductJpaRepository,
    private val studentPassRepository: SpringDataStudentPassJpaRepository,
    private val usageLogRepository: SpringDataPassUsageLogJpaRepository,
) : PassRepository {
    override fun saveProduct(product: PassProduct): PassProduct =
        productRepository.save(PassProductJpaEntity.fromDomain(product)).toDomain()

    override fun findProductsByTenant(tenantId: UUID): List<PassProduct> =
        productRepository.findByTenantIdOrderByNameAsc(tenantId).map { it.toDomain() }

    override fun findProductByTenantAndId(tenantId: UUID, productId: UUID): PassProduct? =
        productRepository.findByTenantIdAndId(tenantId, productId)?.toDomain()

    override fun saveStudentPass(pass: StudentPass): StudentPass =
        studentPassRepository.save(StudentPassJpaEntity.fromDomain(pass)).toDomain()

    override fun findPassesByTenant(tenantId: UUID): List<StudentPass> =
        studentPassRepository.findByTenantIdOrderByExpiresOnDesc(tenantId).map { it.toDomain() }

    override fun findPassesByTenantAndStudent(tenantId: UUID, studentId: UUID): List<StudentPass> =
        studentPassRepository.findByTenantIdAndStudentIdOrderByExpiresOnDesc(tenantId, studentId).map { it.toDomain() }

    override fun findStudentPassByTenantAndId(tenantId: UUID, passId: UUID): StudentPass? =
        studentPassRepository.findByTenantIdAndId(tenantId, passId)?.toDomain()

    override fun appendUsageLog(log: PassUsageLog): PassUsageLog =
        usageLogRepository.save(PassUsageLogJpaEntity.fromDomain(log)).toDomain()

    override fun findUsageLogsByTenantAndPass(tenantId: UUID, passId: UUID): List<PassUsageLog> =
        usageLogRepository.findByTenantIdAndPassIdOrderByCreatedAtDesc(tenantId, passId).map { it.toDomain() }
}
