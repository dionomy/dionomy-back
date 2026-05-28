package com.dionomy.pass.domain

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class StudentPass(
    val id: UUID,
    val tenantId: UUID,
    val productId: UUID,
    val studentId: UUID,
    val totalCount: Int,
    private var usedCountValue: Int,
    val issuedOn: LocalDate,
    val expiresOn: LocalDate,
) {
    init {
        require(totalCount > 0)
        require(usedCountValue in 0..totalCount)
        require(!expiresOn.isBefore(issuedOn))
    }

    val usedCount: Int
        get() = usedCountValue

    val remainingCount: Int
        get() = totalCount - usedCountValue

    fun isExpired(today: LocalDate): Boolean =
        remainingCount <= 0 || !expiresOn.isAfter(today)

    fun consume(count: Int) {
        require(count > 0)
        require(remainingCount >= count)
        usedCountValue += count
    }

    fun restore(count: Int) {
        require(count > 0)
        require(usedCountValue >= count)
        usedCountValue -= count
    }
}

class PassProduct(
    val id: UUID,
    val tenantId: UUID,
    val name: String,
    val totalCount: Int,
    val validDays: Int,
    val price: Long,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    init {
        require(name.isNotBlank())
        require(totalCount > 0)
        require(validDays > 0)
        require(price >= 0)
    }
}

class PassUsageLog(
    val id: UUID,
    val tenantId: UUID,
    val passId: UUID,
    val studentId: UUID,
    val type: PassUsageType,
    val count: Int,
    val reason: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    init {
        require(count > 0)
        require(reason.isNotBlank())
    }
}

enum class PassUsageType {
    CONSUME,
    RESTORE,
}
