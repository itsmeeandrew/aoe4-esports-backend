package net.itsmeeandrew.aoe4esports.common

import java.time.format.DateTimeFormatter

enum class TournamentTier(private val tierName: String) {
    S("S"),
    A("A"),
    UNKNOWN("");

    override fun toString(): String {
        return tierName
    }
}

enum class TournamentFormat(private val formatName: String) {
    ONE_VS_ONE("1v1"),
    TEAM("team"),
    FFA("ffa"),
    UNKNOWN("");

    override fun toString(): String {
        return formatName
    }
}

val liquipediaDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")