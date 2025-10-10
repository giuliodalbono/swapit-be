package io.github.giuliodalbono.swapit.mapper

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule

object ObjectMapperFactory {

    fun jsonMapper(): JsonMapper {
        return configureJsonMapper()
    }

    private fun configureJsonMapper(): JsonMapper {
        return JsonMapper.builder()
            .findAndAddModules()
            .addModule(Jdk8Module())
            .addModule(JavaTimeModule())
            .addModule(ParameterNamesModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true)
            .configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true)
            .configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true)
            .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build()
    }
}