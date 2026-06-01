package com.dionomy.crm.application

import com.dionomy.absence.domain.AbsenceDesiredResult
import com.dionomy.absence.domain.AbsenceRepository
import com.dionomy.attendance.domain.AttendanceRepository
import com.dionomy.crm.domain.CareRecord
import com.dionomy.crm.domain.CareRecordRepository
import com.dionomy.crm.domain.CareRecordStatus
import com.dionomy.crm.domain.RetentionSignal
import com.dionomy.crm.domain.RetentionSignalRecord
import com.dionomy.crm.domain.RetentionSignalRepository
import com.dionomy.crm.domain.RetentionSignalType
import com.dionomy.crm.domain.RiskStudent
import com.dionomy.pass.domain.PassLifecycleStatus
import com.dionomy.pass.domain.PassRepository
import com.dionomy.student.domain.StudentRepository
import com.dionomy.tenant.domain.TenantRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Service
class ListRiskStudentsUseCase(
    private val retentionSignalRepository: RetentionSignalRepository,
    private val refreshRetentionSignalsUseCase: RefreshRetentionSignalsUseCase,
) {
    fun execute(tenantId: UUID): List<RiskStudent> {
        val storedSignals = retentionSignalRepository.findByTenant(tenantId)
            .ifEmpty { refreshRetentionSignalsUseCase.refreshTenant(tenantId) }

        return storedSignals
            .groupBy { it.studentId }
            .map { (studentId, signals) ->
                RiskStudent(
                    studentId = studentId,
                    studentName = signals.first().studentName,
                    signals = signals.map { RetentionSignal(it.type, it.label, it.reason) },
                )
            }
    }
}

@Service
class RefreshRetentionSignalsUseCase(
    private val studentRepository: StudentRepository,
    private val passRepository: PassRepository,
    private val attendanceRepository: AttendanceRepository,
    private val absenceRepository: AbsenceRepository,
    private val retentionSignalRepository: RetentionSignalRepository,
) {
    fun refreshTenant(tenantId: UUID): List<RetentionSignalRecord> {
        val refreshedAt = LocalDateTime.now()
        val signals = calculateSignals(tenantId, refreshedAt)

        retentionSignalRepository.replaceTenantSignals(tenantId, signals)

        return signals
    }

    private fun calculateSignals(tenantId: UUID, refreshedAt: LocalDateTime): List<RetentionSignalRecord> {
        val today = LocalDate.now()

        return studentRepository.findByTenant(tenantId).flatMap { student ->
            val passes = passRepository.findPassesByTenantAndStudent(tenantId, student.id)
            val attendance = attendanceRepository.findByTenantAndStudent(tenantId, student.id)
            val absenceRequests = absenceRepository.findByTenantAndStudent(tenantId, student.id)
            buildList {
                if (attendance.none() && student.createdAt.isBefore(LocalDateTime.now().minusDays(14))) {
                    add(student.toSignal(RetentionSignalType.DORMANT, "휴면", "최근 출석 기록이 없습니다.", refreshedAt))
                }
                if (passes.any { it.lifecycle(today).status == PassLifecycleStatus.EXPIRING_SOON }) {
                    add(student.toSignal(RetentionSignalType.PASS_EXPIRING_SOON, "만료 임박", "7일 이내 만료되는 수강권이 있습니다.", refreshedAt))
                }
                if (absenceRequests.size >= 3) {
                    add(student.toSignal(RetentionSignalType.FREQUENT_CHANGES, "변동 잦음", "결석/변경 요청이 누적되었습니다.", refreshedAt))
                }
                if (absenceRequests.count { it.desiredResult == AbsenceDesiredResult.MAKEUP } >= 2) {
                    add(student.toSignal(RetentionSignalType.MAKEUP_ACCUMULATED, "보강 누적", "보강 요청이 반복되었습니다.", refreshedAt))
                }
                if (student.createdAt.isAfter(LocalDateTime.now().minusDays(30))) {
                    add(student.toSignal(RetentionSignalType.NEW_SETTLING, "신규 정착 중", "첫 달 적응 관찰 대상입니다.", refreshedAt))
                }
            }
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
class RetentionSignalBatch(
    private val tenantRepository: TenantRepository,
    private val refreshRetentionSignalsUseCase: RefreshRetentionSignalsUseCase,
) {
    @Scheduled(cron = "0 0 4 * * *")
    fun refreshSignals() {
        tenantRepository.findAll().forEach { tenant ->
            refreshRetentionSignalsUseCase.refreshTenant(tenant.id)
        }
    }
}

private fun com.dionomy.student.domain.Student.toSignal(
    type: RetentionSignalType,
    label: String,
    reason: String,
    refreshedAt: LocalDateTime,
): RetentionSignalRecord =
    RetentionSignalRecord(
        id = UUID.randomUUID(),
        tenantId = tenantId,
        studentId = id,
        studentName = name,
        type = type,
        label = label,
        reason = reason,
        refreshedAt = refreshedAt,
    )
