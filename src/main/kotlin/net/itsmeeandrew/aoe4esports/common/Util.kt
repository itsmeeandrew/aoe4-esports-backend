package net.itsmeeandrew.aoe4esports.common

import net.itsmeeandrew.aoe4esports.model.Tournament
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

fun timeout(seconds: Int) {
    for (i in seconds downTo 0) {
        val loadingBar = "#".repeat(seconds - i) + "-".repeat(i)
        print("Timeout: [$loadingBar]\r")
        Thread.sleep(1000)
    }
    print("\n")
}

fun isDateTimeUpcoming(date: LocalDate?, time: LocalTime?): Boolean {
    if (date == null || time == null) return false

    val dateTime = LocalDateTime.of(date, time)
    val now = LocalDateTime.now(ZoneOffset.UTC)

    return dateTime.isAfter(now)
}

fun isTournamentOngoing(tournament: Tournament): Boolean {
    val today = LocalDate.now()
    return (tournament.startDate.isEqual(today) || tournament.startDate.isBefore(today)) && (tournament.endDate.isEqual(today) || tournament.endDate.isAfter(today))
}

fun isTournamentUpcoming(tournament: Tournament): Boolean {
    return tournament.startDate.isAfter(LocalDate.now())
}