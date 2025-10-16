package io.github.giuliodalbono.swapit.mapper

import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventAttendee
import com.google.api.services.calendar.model.EventDateTime
import io.github.giuliodalbono.swapit.dto.GCalendarEventDto
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.TimeZone

object GCalendarMapper {
    fun toGoogleEvent(dto: GCalendarEventDto): Event {
        val event: Event = Event()
            .setSummary(dto.summary)
            .setDescription(dto.description)
            .setLocation(dto.location)

        val start = EventDateTime()
            .setDateTime(DateTime(dto.startDateTime!!.toInstant().toEpochMilli()))
            .setTimeZone(dto.timeZone)

        val end = EventDateTime()
            .setDateTime(DateTime(dto.endDateTime!!.toInstant().toEpochMilli()))
            .setTimeZone(dto.timeZone)

        event.setStart(start)
        event.setEnd(end)

        if (dto.attendees != null && dto.attendees!!.isNotEmpty()) {
            event.setAttendees(
                dto.attendees!!
                    .map { email -> EventAttendee().setEmail(email) }
                    .toList()
            )
        }

        return event
    }

    fun toGCalendarDto(event: Event): GCalendarEventDto {
        val start = event.start
        val end = event.end

        val startZoned = start?.dateTime?.let {
            ZonedDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(it.value),
                ZoneId.of(start.timeZone ?: TimeZone.getDefault().id)
            )
        }

        val endZoned = end?.dateTime?.let {
            ZonedDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(it.value),
                ZoneId.of(end.timeZone ?: TimeZone.getDefault().id)
            )
        }

        val attendees = event.attendees?.mapNotNull { it.email }

        return GCalendarEventDto(
            summary = event.summary,
            description = event.description,
            location = event.location,
            startDateTime = startZoned,
            endDateTime = endZoned,
            timeZone = start?.timeZone ?: TimeZone.getDefault().id,
            attendees = attendees
        )
    }
}