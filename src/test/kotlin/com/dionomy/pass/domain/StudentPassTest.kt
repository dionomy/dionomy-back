package com.dionomy.pass.domain

import kotlin.test.Test
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
            studentId = UUID.randomUUID(),
            totalCount = 10,
            usedCount = 10,
            expiresOn = LocalDate.now().plusDays(10),
        )

        assertTrue(pass.isExpired(LocalDate.now()))
    }

    @Test
    fun `pass remains active when count and period are valid`() {
        val pass = StudentPass(
            id = UUID.randomUUID(),
            tenantId = UUID.randomUUID(),
            studentId = UUID.randomUUID(),
            totalCount = 10,
            usedCount = 3,
            expiresOn = LocalDate.now().plusDays(10),
        )

        assertFalse(pass.isExpired(LocalDate.now()))
    }
}
