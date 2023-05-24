package net.itsmeeandrew.aoe4esports.model

import java.time.LocalDate
import java.time.LocalTime

data class Series(
    val awayPlayerId: Int,
    val awayScore: Int,
    val date: LocalDate?,
    val id: Int?,
    val homePlayerId: Int,
    val homeScore: Int,
    val time: LocalTime?,
    val tournamentRoundId: String,
    val tournamentRoundPhaseId: Int
)