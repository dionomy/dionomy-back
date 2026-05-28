package com.dionomy.student.application

import com.dionomy.student.domain.Student
import com.dionomy.student.domain.StudentRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class RegisterStudentUseCase(
    private val studentRepository: StudentRepository,
) {
    fun execute(command: RegisterStudentCommand): Student =
        studentRepository.save(
            Student(
                id = UUID.randomUUID(),
                tenantId = command.tenantId,
                name = command.name,
                phone = command.phone,
                memo = command.memo,
                tags = command.tags,
            ),
        )
}

data class RegisterStudentCommand(
    val tenantId: UUID,
    val name: String,
    val phone: String,
    val memo: String?,
    val tags: List<String>,
)
