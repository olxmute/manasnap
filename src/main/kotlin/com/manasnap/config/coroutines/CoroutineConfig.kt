package com.manasnap.config.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CoroutineConfig {
    @Bean
    fun coroutineScope(): CoroutineScope = CoroutineScope(Dispatchers.Default)
}
