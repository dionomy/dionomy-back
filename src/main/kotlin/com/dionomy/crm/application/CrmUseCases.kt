package com.dionomy.crm.application

import com.dionomy.absence.domain.AbsenceDesiredResult
import com.dionomy.absence.domain.AbsenceRepository
import com.dionomy.attendance.domain.AttendanceRepository
import com.dionomy.crm.domain.CareRecord
import com.dionomy.crm.domain.CareRecordRepository
import com.dionomy.crm.domain.CareRecordStatus
import com.dionomy.crm.domain.RetentionSignal
import com.dionomy.crm.domain.RetentionSignalType
import com.dionomy.crm.domain.RiskStudent
import com.dionomy.pass.domain.PassRepository
import com.dionomy.student.domain.StudentRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Service
class ListRiskStudentsUseCase(
    private val studentRepository: StudentRepository,
    private val passRepository: PassRepository,
    private val attendanceRepository: AttendanceRepository,
    private val absenceRepository: AbsenceRepository,
) {
    fun execute(tenantId: UUID): List<RiskStudent> {
        val today = LocalDate.now()

        return studentRepository.findByTenant(tenantId).mapNotNull { student ->
            val passes = passRepository.findPassesByTenantAndStudent(tenantId, student.id)
            val attendance = attendanceRepository.findByTenantAndStudent(tenantId, student.id)
            val absenceRequests = absenceRepository.findByTenantAndStudent(tenantId, student.id)
            val signals = buildList {
                if (attendance.none() && student.createdAt.isBefore(LocalDateTime.now().minusDays(14))) {
                    add(RetentionSignal(RetentionSignalType.DORMANT, "휴면", "최근 출석 기록이 없습니다."))
                }
                if (passes.any { !it.isExpired(today) && it.expiresOn <= today.plusDays(7) }) {
                    add(RetentionSignal(RetentionSignalType.PASS_EXPIRING_SOON, "만료 임박", "7일 이내 만료되는 수강권이 있습니다."))
                }
                if (absenceRequests.size >= 3) {
                    add(RetentionSignal(RetentionSignalType.FREQUENT_CHANGES, "변동 잦음", "결석/변경 요청이 누적되었습니다."))
                }
                if (absenceRequests.count { it.desiredResult == AbsenceDesiredResult.MAKEUP } >= 2) {
                    add(RetentionSignal(RetentionSignalType.MAKEUP_ACCUMULATED, "보강 누적", "보강 요청이 반복되었습니다."))
                }
                if (student.createdAt.isAfter(LocalDateTime.now().minusDays(30))) {
                    add(RetentionSignal(RetentionSignalType.NEW_SETTLING, "신규 정착 중", "첫 달 적응 관찰 대상입니다."))
                }
            }

            if (signals.isEmpty()) null else RiskStudent(student.id, student.name, signals)
        }
    }
}

@Service
class CareRecordUseCases(
    private val careRecordRepository: CareRecordRepository,
) {
    fun list(tenantId: UUID, studentId: UUID): List<CareRecord> =
        careRecordRepository.findByTenantAndStudent(tenantId, studentId)

    fun create(tenantId: UUID, studentId: UUID, memo: String, status: CareRecordStatus): CareRecord =
        careRecordRepository.save(
            CareRecord(
                id = UUID.randomUUID(),
                tenantId = tenantId,
                studentId = studentId,
                memo = memo,
                status = status,
            ),
        )
}

@Component
class RetentionSignalBatch {
    @Scheduled(cron = "0 0 4 * * *")
    fun refreshSignals() {
    }
}
