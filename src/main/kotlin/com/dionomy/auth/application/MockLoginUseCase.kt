package com.dionomy.auth.application

import com.dionomy.auth.domain.UserAccount
import com.dionomy.auth.domain.UserRole
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class MockLoginUseCase {
    fun execute(role: UserRole): MockLoginResult {
        val user = UserAccount(
            id = UUID.randomUUID(),
            tenantId = if (role == UserRole.DIONOMY_ADMIN) null else DEFAULT_TENANT_ID,
            name = role.displayName(),
            role = role,
        )

        return MockLoginResult(
            token = "mock-${role.name.lowercase()}-${user.id}",
            user = user,
        )
    }

    private fun UserRole.displayName(): String =
        when (this) {
            UserRole.DIONOMY_ADMIN -> "Dionomy 관리자"
            UserRole.OWNER -> "원장"
            UserRole.TEACHER -> "강사"
            UserRole.STUDENT -> "수강생"
        }

    companion object {
        val DEFAULT_TENANT_ID: UUID = UUID.fromString("00000000-0000-0000-0000-000000000001")
    }
}

data class MockLoginResult(
    val token: String,
    val user: UserAccount,
)
