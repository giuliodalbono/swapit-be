package io.github.giuliodalbono.swapit

import org.springframework.boot.ApplicationContextFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.runApplication
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Import
import java.util.*

@SpringBootApplication
class SwapItBeApplication {
    companion object {
        const val APPLICATION_NAME = "SwapItBe"
    }
}

fun main(args: Array<String>) {
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Rome"))
    if (args.isNotEmpty() && listOf(*args).contains("liquibase")) {
        SpringApplicationBuilder(LiquibaseInit::class.java)
            .contextFactory(ApplicationContextFactory.ofContextClass(AnnotationConfigApplicationContext::class.java))
            .profiles("liquibase")
            .run(*args)
        return
    }
    runApplication<SwapItBeApplication>(*args)
}

@Import(DataSourceAutoConfiguration::class, LiquibaseAutoConfiguration::class)
internal class LiquibaseInit