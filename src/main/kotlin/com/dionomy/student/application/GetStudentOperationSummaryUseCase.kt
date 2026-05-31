package com.dionomy.student.application

import com.dionomy.pass.domain.PassRepository
import com.dionomy.pass.domain.StudentPass
import com.dionomy.student.domain.StudentRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.UUID

@Service
class GetStudentOperationSummaryUseCase(
    private val studentRepository: StudentRepository,
    private val passRepository: PassRepository,
) {
    fun execute(tenantId: UUID): StudentOperationSummary {
        val today = LocalDate.now()
        val students = studentRepository.findByTenant(tenantId)
        val passesByStudent = passRepository.findPassesByTenant(tenantId).groupBy { it.studentId }
        val studentSummaries = students.map { student ->
            val activePass = passesByStudent[student.id]
                ?.filter { !it.isExpired(today) }
                ?.maxByOrNull { it.expiresOn }

            StudentPassSummary(
                studentId = student.id,
                activePassId = activePass?.id,
                remainingCount = activePass?.remainingCount,
                totalCount = activePass?.totalCount,
                expiresOn = activePass?.expiresOn,
                expiringSoon = activePass?.isExpiringSoon(today) ?: false,
                lowRemaining = activePass?.isLowRemaining() ?: false,
            )
        }

        return StudentOperationSummary(
            totalStudents = students.size,
            passExpiringSoonCount = studentSummaries.count { it.expiringSoon },
            passLowRemainingCount = studentSummaries.count { it.lowRemaining },
            students = studentSummaries,
        )
    }
}

data class StudentOperationSummary(
    val totalStudents: Int,
    val passExpiringSoonCount: Int,
    val passLowRemainingCount: Int,
    val students: List<StudentPassSummary>,
)

data class StudentPassSummary(
    val studentId: UUID,
    val activePassId: UUID?,
    val remainingCount: Int?,
    val totalCount: Int?,
    val expiresOn: LocalDate?,
    val expiringSoon: Boolean,
    val lowRemaining: Boolean,
)

private fun StudentPass.isExpiringSoon(today: LocalDate): Boolean =
    !isExpired(today) && expiresOn <= today.plusDays(7)

private fun StudentPass.isLowRemaining(): Boolean =
    remainingCount in 1..2
