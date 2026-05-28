package com.dionomy.student.application

import com.dionomy.student.domain.Student
import com.dionomy.student.domain.StudentRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetStudentUseCase(
    private val studentRepository: StudentRepository,
) {
    fun execute(tenantId: UUID, studentId: UUID): Student =
        requireNotNull(studentRepository.findByTenantAndId(tenantId, studentId))
}
