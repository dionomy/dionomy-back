package com.dionomy.schedule.application

import com.dionomy.schedule.domain.ClassSession
import com.dionomy.schedule.domain.ScheduleRepository
import org.springframework.stereotype.Service

@Service
class CreateClassSessionUseCase(
    private val scheduleRepository: ScheduleRepository,
) {
    fun execute(session: ClassSession): ClassSession = scheduleRepository.save(session)
}
