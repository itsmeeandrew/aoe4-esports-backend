package net.itsmeeandrew.aoe4esports.model

data class Match(
    val awayCivilizationId: Int?,
    val homeCivilizationId: Int?,
    val id: Int?,
    val mapId: Int?,
    val seriesId: Int?,
    val winnerPlayerId: Int
)
