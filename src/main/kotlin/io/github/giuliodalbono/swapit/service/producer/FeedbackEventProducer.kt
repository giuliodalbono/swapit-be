package io.github.giuliodalbono.swapit.service.producer

import io.github.giuliodalbono.swapit.dto.FeedbackDto
import io.github.giuliodalbono.swapit.service.CloudEventContext
import io.github.giuliodalbono.swapit.service.CloudEventService
import org.springframework.stereotype.Service

@Service
class FeedbackEventProducer(
    private val cloudEventService: CloudEventService
) {

    companion object {
        const val RATE_TYPE = "Rate"
        const val SKILL_SUBJECT = "Skill"
        const val FEEDBACK_TOPIC = "FeedbackEvent"
    }

    fun produceRateSkillEvent(feedback: FeedbackDto) {
        val cloudEventContext = CloudEventContext(subject = SKILL_SUBJECT, type = RATE_TYPE, data = feedback)
        cloudEventService.sendCloudEventAfterCommit(cloudEventContext, FEEDBACK_TOPIC)
    }
}