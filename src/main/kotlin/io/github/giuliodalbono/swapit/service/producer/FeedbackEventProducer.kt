package io.github.giuliodalbono.swapit.service.producer

import io.github.giuliodalbono.swapit.dto.FeedbackDto
import io.github.giuliodalbono.swapit.service.CloudEventContext
import io.github.giuliodalbono.swapit.service.CloudEventService
import io.github.giuliodalbono.swapit.service.UserService
import org.springframework.stereotype.Service

@Service
class FeedbackEventProducer(
    private val userService: UserService,
    private val cloudEventService: CloudEventService
) {

    companion object {
        const val RATE_TYPE = "Rate"
        const val SKILL_SUBJECT = "Skill"
        const val FEEDBACK_TOPIC = "FeedbackEvent"
    }

    fun produceRateSkillEvent(feedback: FeedbackDto) {
        val skillRateEvent = feedback.toSkillRateEvent(userService)
        val cloudEventContext = CloudEventContext(subject = SKILL_SUBJECT, type = RATE_TYPE, data = skillRateEvent)
        cloudEventService.sendCloudEventAfterCommit(cloudEventContext, FEEDBACK_TOPIC)
    }
}

data class SkillRateEvent(val rating: Long, val review: String, val reviewerUid: String, val reviewedUid: String, val skills: Set<String>)

fun FeedbackDto.toSkillRateEvent(userService: UserService) = SkillRateEvent(
    rating = this@toSkillRateEvent.rating,
    review = this@toSkillRateEvent.review,
    reviewerUid = this@toSkillRateEvent.reviewerUid!!,
    reviewedUid = this@toSkillRateEvent.reviewedUid!!,
    skills = userService.findByUid(this@toSkillRateEvent.reviewedUid)
        .orElseThrow()
        .skillOffered!!
)