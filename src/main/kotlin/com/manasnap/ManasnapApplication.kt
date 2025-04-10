package com.manasnap

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@EnableAsync
@SpringBootApplication
class ManasnapApplication

fun main(args: Array<String>) {
	runApplication<ManasnapApplication>(*args)
}
