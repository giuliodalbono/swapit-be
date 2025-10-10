package io.github.giuliodalbono.swapit.service

import io.github.giuliodalbono.swapit.dto.SkillDto
import org.springframework.stereotype.Service

@Service
class SkillEventProducer(
    private val cloudEventService: CloudEventService
) {

    companion object {
        const val CREATE_TYPE = "Create"
        const val UPDATE_TYPE = "Update"
        const val SKILL_SUBJECT = "Skill"
        const val SKILL_TOPIC = "SkillEvent"
    }

    fun produceCreateSkillEvent(skill: SkillDto) {
        val cloudEventContext = CloudEventContext(subject = SKILL_SUBJECT, type = CREATE_TYPE, data = skill)
        cloudEventService.sendCloudEvent(cloudEventContext, SKILL_TOPIC)
    }

    fun produceUpdateSkillEvent(skill: SkillDto) {
        val cloudEventContext = CloudEventContext(subject = SKILL_SUBJECT, type = UPDATE_TYPE, data = skill)
        cloudEventService.sendCloudEvent(cloudEventContext, SKILL_TOPIC)
    }
}