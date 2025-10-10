package io.github.giuliodalbono.swapit.service

import io.github.giuliodalbono.swapit.SwapItBeApplication
import io.github.giuliodalbono.swapit.annotation.AfterCommit
import io.github.giuliodalbono.swapit.mapper.ObjectMapperFactory
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.cloud.function.cloudevent.CloudEventMessageBuilder
import org.springframework.cloud.function.cloudevent.CloudEventMessageUtils
import org.springframework.cloud.function.context.message.MessageUtils
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.http.MediaType
import org.springframework.messaging.Message
import org.springframework.stereotype.Service
import java.net.URI
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

@Service
class CloudEventService(
    val streamBridge: StreamBridge
) {
    private val log = KotlinLogging.logger {}

    companion object {
        const val CLOUD_EVENT_VERSION_VALUE = "1.0"
    }

    fun sendCloudEvent(cloudEventContext: CloudEventContext, bindingName: String): Message<String> {
        val event = buildMessage(cloudEventContext)
        log.info { "Publishing to topic: $bindingName \n $event" }
        streamBridge.send(bindingName, event)
        return event
    }

    @AfterCommit
    fun sendCloudEventAfterCommit(cloudEventContext: CloudEventContext, bindingName: String): Message<String> {
        return sendCloudEvent(cloudEventContext, bindingName)
    }

    private fun buildMessage(cloudEventContext: CloudEventContext): Message<String> {
        return CloudEventMessageBuilder
            .withData(cloudEventContext.getDataAsString())
            .setDataContentType(MediaType.APPLICATION_JSON_VALUE)
            .setSpecVersion(cloudEventContext.version ?: CLOUD_EVENT_VERSION_VALUE)
            .setId(UUID.randomUUID().toString())
            .setSubject(cloudEventContext.subject!!)
            .setSource(URI.create(cloudEventContext.source ?: SwapItBeApplication.APPLICATION_NAME))
            .setType(cloudEventContext.type!!)
            .setTime(if (cloudEventContext.date != null) cloudEventContext.date!!.atOffset(ZoneOffset.UTC) else OffsetDateTime.now())
            .setHeader(MessageUtils.MESSAGE_TYPE, CloudEventMessageUtils.CLOUDEVENT_VALUE)
            .build(CloudEventMessageUtils.KAFKA_ATTR_PREFIX)
    }
}

data class CloudEventContext(
    var version: String? = null,
    var source: String? = null,
    var subject: String? = null,
    var type: String? = null,
    var date: LocalDateTime? = null,
    var data: Any? = null
) {
    fun getDataAsString(): String = data as? String ?: ObjectMapperFactory.jsonMapper().writeValueAsString(data)
}