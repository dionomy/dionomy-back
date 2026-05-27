package com.dionomy.auth.presentation

import com.dionomy.auth.application.MockLoginUseCase
import com.dionomy.auth.domain.UserRole
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val mockLoginUseCase: MockLoginUseCase,
) {
    @PostMapping("/mock-login")
    fun mockLogin(@Valid @RequestBody request: MockLoginRequest): MockLoginResponse {
        val result = mockLoginUseCase.execute(request.role)

        return MockLoginResponse(
            token = result.token,
            user = AuthUserResponse(
                id = result.user.id,
                tenantId = result.user.tenantId,
                name = result.user.name,
                role = result.user.role,
            ),
        )
    }
}

data class MockLoginRequest(
    @field:NotNull
    val role: UserRole,
)

data class MockLoginResponse(
    val token: String,
    val user: AuthUserResponse,
)

data class AuthUserResponse(
    val id: UUID,
    val tenantId: UUID?,
    val name: String,
    val role: UserRole,
)
