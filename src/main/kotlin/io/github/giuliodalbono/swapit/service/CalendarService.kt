package io.github.giuliodalbono.swapit.service

import com.google.api.client.auth.oauth2.TokenResponseException
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory.*
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventReminder
import io.github.giuliodalbono.swapit.SwapItBeApplication
import io.github.giuliodalbono.swapit.annotation.AfterCommit
import io.github.giuliodalbono.swapit.dto.GCalendarEventDto
import io.github.giuliodalbono.swapit.mapper.GCalendarMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.time.Duration


@Service
class CalendarService {
    private val logger = KotlinLogging.logger {}

    companion object {
        const val ALL = "all"
        const val EMAIL = "email"
        const val POPUP = "popup"
        const val CALENDAR_ID = "dalbonogiulio@gmail.com"
        const val TOKENS_FILE_PATH = "email-credentials/tokens"
        const val CREDENTIALS_FILE_PATH = "email-credentials/swapit-be-oauth2-credentials.json"

        val EMAIL_REMINDER_TIME = Duration.ofHours(24).toMinutes().toInt()
        val POPUP_REMINDER_TIME = Duration.ofMinutes(10).toMinutes().toInt()
    }

    private fun <T> executeHandlingTokenResponseException(operation: () -> T): T {
        return try {
            operation()
        } catch (e: TokenResponseException) {
            if (e.statusCode == 400 && e.details?.error == "invalid_grant") {
                logger.warn { "Invalid grant error detected. Clearing stored credentials and retrying..." }
                clearStoredCredentials()
                // Retry the operation
                operation()
            } else {
                throw e
            }
        }
    }

    private fun clearStoredCredentials() {
        val tokenDir = File(TOKENS_FILE_PATH)
        val storedCredentialFile = File(tokenDir, "StoredCredential")
        if (storedCredentialFile.exists()) {
            storedCredentialFile.delete()
            logger.info { "Deleted invalid StoredCredential file" }
        }
    }

    fun buildCalendarServiceUsingOAuth2(): Calendar {
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val jsonFactory = getDefaultInstance()

        val clientSecrets = GoogleClientSecrets.load(
            jsonFactory,
            InputStreamReader(FileInputStream(CREDENTIALS_FILE_PATH))
        )

        val scopes = listOf(CalendarScopes.CALENDAR)

        val tokenDir = File(TOKENS_FILE_PATH)
        val dataStoreFactory = FileDataStoreFactory(tokenDir)

        val flow = GoogleAuthorizationCodeFlow.Builder(
            httpTransport,
            jsonFactory,
            clientSecrets,
            scopes
        )
            .setDataStoreFactory(dataStoreFactory)
            .setAccessType("offline")
            .build()

        val localReceiver = LocalServerReceiver.Builder().setPort(8888).build()

        return executeHandlingTokenResponseException {
            val credential = AuthorizationCodeInstalledApp(flow, localReceiver).authorize("user")
            Calendar.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(SwapItBeApplication.APPLICATION_NAME)
                .build()
        }
    }

    fun getEventsUsingOAuth2(): List<GCalendarEventDto> {
        logger.info { "Getting all calendar events" }
        return executeHandlingTokenResponseException {
            val calendar = buildCalendarServiceUsingOAuth2()
            var pageToken: String? = null
            val eventListResponse = mutableListOf<Event>()

            do {
                val eventList = calendar.events().list(CALENDAR_ID).setPageToken(pageToken).execute()
                pageToken = eventList.nextPageToken

                eventList.items.forEach { eventListResponse.add(it) }
            } while (pageToken != null)

            logger.info { "Found ${eventListResponse.size} calendar events" }

            eventListResponse.map { GCalendarMapper.toGCalendarDto(it) }
        }
    }

    fun createEventUsingOAuth2(calendarEventDto: GCalendarEventDto) {
        logger.info { "Creating calendar event with calendarEventDto requested: $calendarEventDto" }

        executeHandlingTokenResponseException {
            val calendar = buildCalendarServiceUsingOAuth2()

            val event = GCalendarMapper.toGoogleEvent(calendarEventDto)

            val reminders = Event.Reminders().setUseDefault(false).setOverrides(
                listOf(
                    EventReminder().setMethod(EMAIL).setMinutes(EMAIL_REMINDER_TIME),
                    EventReminder().setMethod(POPUP).setMinutes(POPUP_REMINDER_TIME)
                )
            )
            event.reminders = reminders

            val createdEvent = calendar.events().insert(CALENDAR_ID, event)
                .setSendUpdates(ALL)
                .execute()

            logger.info { "Event created: ${createdEvent.htmlLink}" }
        }
    }

    @AfterCommit
    fun createEventUsingOAuth2AfterCommit(calendarEventDto: GCalendarEventDto) {
        createEventUsingOAuth2(calendarEventDto)
    }
}