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

    fun lifecycle(today: LocalDate): PassLifecycle {
        if (remainingCount <= 0) {
            return PassLifecycle(PassLifecycleStatus.USED_UP, PassExpirationReason.COUNT_EXHAUSTED)
        }

        if (!expiresOn.isAfter(today)) {
            return PassLifecycle(PassLifecycleStatus.EXPIRED, PassExpirationReason.PERIOD_EXPIRED)
        }

        if (expiresOn <= today.plusDays(7)) {
            return PassLifecycle(PassLifecycleStatus.EXPIRING_SOON, PassExpirationReason.PERIOD_EXPIRING_SOON)
        }

        if (remainingCount <= 2) {
            return PassLifecycle(PassLifecycleStatus.EXPIRING_SOON, PassExpirationReason.COUNT_LOW)
        }

        return PassLifecycle(PassLifecycleStatus.ACTIVE, null)
    }

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

data class PassLifecycle(
    val status: PassLifecycleStatus,
    val reason: PassExpirationReason?,
)

enum class PassLifecycleStatus {
    ACTIVE,
    EXPIRING_SOON,
    EXPIRED,
    USED_UP,
}

enum class PassExpirationReason {
    PERIOD_EXPIRED,
    COUNT_EXHAUSTED,
    PERIOD_EXPIRING_SOON,
    COUNT_LOW,
}
