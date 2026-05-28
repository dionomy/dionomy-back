package com.dionomy.student.application

import com.dionomy.student.domain.Student
import com.dionomy.student.domain.StudentRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ListStudentsUseCase(
    private val studentRepository: StudentRepository,
) {
    fun execute(tenantId: UUID): List<Student> =
        studentRepository.findByTenant(tenantId)
}
