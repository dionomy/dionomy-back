package com.dionomy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DionomyApplication

fun main(args: Array<String>) {
    runApplication<DionomyApplication>(*args)
}
