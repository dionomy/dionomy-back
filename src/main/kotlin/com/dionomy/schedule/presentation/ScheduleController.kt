package com.dionomy.schedule.presentation

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/schedules")
class ScheduleController {
    @GetMapping
    fun list(): List<ScheduleSummaryResponse> =
        listOf(
            ScheduleSummaryResponse(time = "10:00", title = "바이올린 그룹 A", capacity = "3/5"),
            ScheduleSummaryResponse(time = "14:00", title = "1:1 레슨", capacity = "김민지"),
        )
}

data class ScheduleSummaryResponse(
    val time: String,
    val title: String,
    val capacity: String,
)
