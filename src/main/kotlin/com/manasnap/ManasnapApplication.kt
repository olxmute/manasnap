package com.manasnap

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableAsync

@EnableAsync
@EnableJpaAuditing
@SpringBootApplication
@ConfigurationPropertiesScan
class ManasnapApplication

fun main(args: Array<String>) {
	runApplication<ManasnapApplication>(*args)
}
