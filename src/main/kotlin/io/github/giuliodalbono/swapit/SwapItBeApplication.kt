package io.github.giuliodalbono.swapit

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SwapItBeApplication

fun main(args: Array<String>) {
    runApplication<SwapItBeApplication>(*args)
}
