package com.dionomy.pass.infrastructure

import com.dionomy.pass.domain.PassProduct
import com.dionomy.pass.domain.PassRepository
import com.dionomy.pass.domain.PassUsageLog
import com.dionomy.pass.domain.StudentPass
import org.springframework.stereotype.Repository
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Repository
class InMemoryPassRepository : PassRepository {
    private val products = ConcurrentHashMap<UUID, PassProduct>()
    private val passes = ConcurrentHashMap<UUID, StudentPass>()
    private val usageLogs = ConcurrentHashMap<UUID, PassUsageLog>()

    override fun saveProduct(product: PassProduct): PassProduct {
        products[product.id] = product
        return product
    }

    override fun findProductsByTenant(tenantId: UUID): List<PassProduct> =
        products.values
            .filter { it.tenantId == tenantId }
            .sortedBy { it.name }

    override fun findProductByTenantAndId(tenantId: UUID, productId: UUID): PassProduct? =
        products[productId]?.takeIf { it.tenantId == tenantId }

    override fun saveStudentPass(pass: StudentPass): StudentPass {
        passes[pass.id] = pass
        return pass
    }

    override fun findPassesByTenantAndStudent(tenantId: UUID, studentId: UUID): List<StudentPass> =
        passes.values
            .filter { it.tenantId == tenantId && it.studentId == studentId }
            .sortedByDescending { it.expiresOn }

    override fun findStudentPassByTenantAndId(tenantId: UUID, passId: UUID): StudentPass? =
        passes[passId]?.takeIf { it.tenantId == tenantId }

    override fun appendUsageLog(log: PassUsageLog): PassUsageLog {
        usageLogs[log.id] = log
        return log
    }

    override fun findUsageLogsByTenantAndPass(tenantId: UUID, passId: UUID): List<PassUsageLog> =
        usageLogs.values
            .filter { it.tenantId == tenantId && it.passId == passId }
            .sortedByDescending { it.createdAt }
}
