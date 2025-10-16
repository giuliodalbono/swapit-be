package io.github.giuliodalbono.swapit.dto

import java.time.ZonedDateTime

data class GCalendarEventDto(
    val summary: String? = null,
    var description: String? = null,
    var location: String? = null,
    var startDateTime: ZonedDateTime? = null,
    var endDateTime: ZonedDateTime? = null,
    var timeZone: String? = null,
    var attendees: List<String>? = null
)