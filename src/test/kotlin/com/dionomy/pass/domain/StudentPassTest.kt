package com.dionomy.pass.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import java.time.LocalDate
import java.util.UUID

class StudentPassTest {
    @Test
    fun `pass expires when count is exhausted`() {
        val pass = StudentPass(
            id = UUID.randomUUID(),
            tenantId = UUID.randomUUID(),
            productId = UUID.randomUUID(),
            studentId = UUID.randomUUID(),
            totalCount = 10,
            usedCountValue = 10,
            issuedOn = LocalDate.now().minusDays(10),
            expiresOn = LocalDate.now().plusDays(10),
        )

        assertTrue(pass.isExpired(LocalDate.now()))
    }

    @Test
    fun `pass remains active when count and period are valid`() {
        val pass = StudentPass(
            id = UUID.randomUUID(),
            tenantId = UUID.randomUUID(),
            productId = UUID.randomUUID(),
            studentId = UUID.randomUUID(),
            totalCount = 10,
            usedCountValue = 3,
            issuedOn = LocalDate.now().minusDays(10),
            expiresOn = LocalDate.now().plusDays(10),
        )

        assertFalse(pass.isExpired(LocalDate.now()))
    }

    @Test
    fun `pass lifecycle is expiring soon when period is within seven days`() {
        val pass = StudentPass(
            id = UUID.randomUUID(),
            tenantId = UUID.randomUUID(),
            productId = UUID.randomUUID(),
            studentId = UUID.randomUUID(),
            totalCount = 10,
            usedCountValue = 3,
            issuedOn = LocalDate.now().minusDays(10),
            expiresOn = LocalDate.now().plusDays(7),
        )

        val lifecycle = pass.lifecycle(LocalDate.now())

        assertEquals(PassLifecycleStatus.EXPIRING_SOON, lifecycle.status)
        assertEquals(PassExpirationReason.PERIOD_EXPIRING_SOON, lifecycle.reason)
    }

    @Test
    fun `pass lifecycle is used up when count is exhausted before period`() {
        val pass = StudentPass(
            id = UUID.randomUUID(),
            tenantId = UUID.randomUUID(),
            productId = UUID.randomUUID(),
            studentId = UUID.randomUUID(),
            totalCount = 10,
            usedCountValue = 10,
            issuedOn = LocalDate.now().minusDays(10),
            expiresOn = LocalDate.now().plusDays(10),
        )

        val lifecycle = pass.lifecycle(LocalDate.now())

        assertEquals(PassLifecycleStatus.USED_UP, lifecycle.status)
        assertEquals(PassExpirationReason.COUNT_EXHAUSTED, lifecycle.reason)
    }
}
