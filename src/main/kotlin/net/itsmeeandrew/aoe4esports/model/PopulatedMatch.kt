package net.itsmeeandrew.aoe4esports.model

data class PopulatedMatch(
    val id: Int,
    val seriesId: Int,
    val homeCivilization: String,
    val awayCivilization: String,
    val winnerPlayer: String,
    val map: String
)