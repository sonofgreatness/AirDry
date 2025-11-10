package com.example.airdry.util

import android.util.Log
import com.example.airdry.util.Constants.Companion.FILTER_DATE_FORMAT
import com.example.airdry.util.Constants.Companion.HUMAN_READABLE_FORMAT_TEXT
import com.example.airdry.util.Constants.Companion.HUMAN_READABLE_FORMAT_TEXT_HOUR_MINUTE
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

class UtilityFunctions {
    companion object {

        /** Converts nullable String to UUID or null */
        fun String?.toUUIDOrNull(): UUID? = runCatching {
            this?.let { UUID.fromString(it) }
        }.getOrNull()

        /** Converts nullable UUID to nullable String */
        fun UUID?.toStringOrNull(): String? = this?.toString()

        /** Converts nullable UUID to readable String */
        fun UUID?.toStringCustom(): String = this?.toString() ?: "null"

        /** Converts non-null String to UUID */
        fun String.toUUID(): UUID = UUID.fromString(this)

        /** Converts Boolean to Long (true=1, false=0) */
        fun convertBoolToLong(value: Boolean): Long = if (value) 1L else 0L

        /** Converts Long to Boolean, throws if not 0 or 1 */
        @Throws(IllegalArgumentException::class)
        fun convertLongToBool(value: Long): Boolean = when (value) {
            1L -> true
            0L -> false
            else -> throw IllegalArgumentException("Expecting 0 or 1 but got $value")
        }

        /** Generates formatted current date in readable form */
        fun generateDayDateForNowSpaced(): String {
            val formatter = SimpleDateFormat("EEEE dd-MM-yyyy", Locale.getDefault())
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            return formatter.format(Date.from(Instant.now()))
        }

        /** Formats an ISO 8601 string into a human-readable date */
        fun formatReadableDate(isoString: String?): String {
            return try {
                if (isoString.isNullOrBlank()) return "-"
                val instant = Instant.parse(isoString)
                val formatter = DateTimeFormatter.ofPattern("EEEE dd/MM/yyyy")
                    .withLocale(Locale.getDefault())
                    .withZone(ZoneOffset.UTC)
                formatter.format(instant)
            } catch (e: Exception) {
                Log.e("UtilityFunctions", "formatReadableDate failed for $isoString", e)
                "-"
            }
        }
        fun formatReadableDateTEXT(isoString: String?): String {
            return try {
                if (isoString.isNullOrBlank()) return "-"
                val instant = Instant.parse(isoString)
                val formatter = DateTimeFormatter.ofPattern(HUMAN_READABLE_FORMAT_TEXT)
                    .withLocale(Locale.getDefault())
                    .withZone(ZoneOffset.UTC)
                formatter.format(instant)
            } catch (e: Exception) {
                Log.e("UtilityFunctions", "formatReadableDate failed for $isoString", e)
                "-"
            }
        }

        /** Common date formatters */
        val customApiFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("EEEE dd/MM/yyyy HH:mm", Locale.getDefault())

         val customApiFormatterTEXT: DateTimeFormatter =
        DateTimeFormatter.ofPattern(HUMAN_READABLE_FORMAT_TEXT_HOUR_MINUTE, Locale.getDefault())

        private val datePickerFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern(FILTER_DATE_FORMAT, Locale.getDefault())

        /** Default timezone: UTC */
        private val utcZoneId: ZoneId = ZoneOffset.UTC

        /** Parses date string from date picker (no time) */
        fun parseStringFromDatePickerToDate(dateString: String): LocalDate? {
            return runCatching { LocalDate.parse(dateString, datePickerFormatter) }.getOrNull()
        }

        /** Parses date string from date picker to UTC Timestamp */
        fun parseStringFromDatePickerToTimeStamp(dateString: String): Timestamp {
            val localDate = LocalDate.parse(dateString, datePickerFormatter)
            val millis = localDate.atStartOfDay(utcZoneId).toInstant().toEpochMilli()
            return Timestamp(millis)
        }

        /** Parses various API date formats into UTC Timestamp */
        fun parseStringFromApiToTimeStamp(dateString: String): Timestamp {
            val cleaned = dateString
                .replace("\\s+".toRegex(), " ")
                .trim()
            val possibleFormats = listOf(
                DateTimeFormatter.ofPattern("EEEE dd/MM/yyyy HH:mm", Locale.getDefault()),
                DateTimeFormatter.ofPattern("EEEE dd/MM/yyyy", Locale.getDefault()),
                DateTimeFormatter.ofPattern(HUMAN_READABLE_FORMAT_TEXT_HOUR_MINUTE, Locale.getDefault()),
                DateTimeFormatter.ofPattern(HUMAN_READABLE_FORMAT_TEXT, Locale.getDefault())
                )
            for (fmt in possibleFormats) {
                try {
                    Log.d("parser", "Trying pattern: $fmt")
                    val localDate = LocalDate.parse(cleaned, fmt)
                    val millis = localDate.atStartOfDay(utcZoneId).toInstant().toEpochMilli()
                    val ts = Timestamp(millis)
                    Log.d("parser", "Successfully parsed: $ts using pattern $fmt")
                    return ts
                } catch (e: Exception) {
                    Log.e("parser", "Failed with pattern $fmt : ${e.message}")
                }
            }
            throw IllegalArgumentException("Unsupported date format: $dateString")
        }
        /** Generates filter date for now (UTC) */
        fun generateNowFilterDate(): String {
            return LocalDate.now(utcZoneId).format(datePickerFormatter)
        }

        /** Generates filter date offset by given months (UTC) */
        fun generateOffSetFilterDate(offsetMonths: Long): String {
            val targetDate = LocalDate.now(utcZoneId).minusMonths(offsetMonths)
            return targetDate.format(datePickerFormatter)
        }

        fun isWorkDateMatchingFilter(workDate: Timestamp, filterDate: String?): Boolean {
            if (filterDate.isNullOrBlank()) return true
            val start = parseStringFromDatePickerToTimeStamp(filterDate)
            val end = Timestamp(start.time + 24 * 60 * 60 * 1000)
            return workDate in start..end
        }


    }
}