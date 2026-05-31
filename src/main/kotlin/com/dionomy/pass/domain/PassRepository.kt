package com.dionomy.pass.domain

import java.util.UUID

interface PassRepository {
    fun saveProduct(product: PassProduct): PassProduct
    fun findProductsByTenant(tenantId: UUID): List<PassProduct>
    fun findProductByTenantAndId(tenantId: UUID, productId: UUID): PassProduct?
    fun saveStudentPass(pass: StudentPass): StudentPass
    fun findPassesByTenant(tenantId: UUID): List<StudentPass>
    fun findPassesByTenantAndStudent(tenantId: UUID, studentId: UUID): List<StudentPass>
    fun findStudentPassByTenantAndId(tenantId: UUID, passId: UUID): StudentPass?
    fun appendUsageLog(log: PassUsageLog): PassUsageLog
    fun findUsageLogsByTenantAndPass(tenantId: UUID, passId: UUID): List<PassUsageLog>
}
