package io.github.giuliodalbono.swapit.controller

import com.google.api.services.calendar.model.Event
import io.github.giuliodalbono.swapit.dto.GCalendarEventDto
import io.github.giuliodalbono.swapit.service.CalendarService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/gcalendar/events")
@Tag(name = "Google Calendar Management", description = "APIs for managing google calendar")
class CalendarController(private val calendarService: CalendarService) {

    @GetMapping
    @Operation(
        summary = "Get all events",
        description = "Gat all the events from the calendar"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved list of events",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = Array<Event>::class)
                )]
            )
        ]
    )
    fun getCalendarEvents(): ResponseEntity<List<GCalendarEventDto>> {
            val calResponse: List<GCalendarEventDto> = calendarService.getEventsUsingOAuth2()
            return ResponseEntity<List<GCalendarEventDto>>(calResponse, HttpStatus.OK)
        }

    @PostMapping
    @Operation(
        summary = "Create new event",
        description = "Creates a new event entry in the calendar"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Event created successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = GCalendarEventDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid input data",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun createCalendarEvent(@Valid @RequestBody eventRequest: GCalendarEventDto): ResponseEntity<Void> {
        calendarService.createEventUsingOAuth2(eventRequest)
        return ResponseEntity<Void>(HttpStatus.CREATED)
    }
}