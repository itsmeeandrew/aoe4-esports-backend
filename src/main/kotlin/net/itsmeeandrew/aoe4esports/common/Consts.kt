package net.itsmeeandrew.aoe4esports.common

import com.fasterxml.jackson.annotation.JsonValue
import java.time.format.DateTimeFormatter

val liquipediaDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")

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

    @JsonValue
    override fun toString(): String {
        return formatName
    }

    companion object {
        fun from(name: String): TournamentFormat {
            return when (name) {
                "1v1" -> ONE_VS_ONE
                "team" -> TEAM
                "ffa" -> FFA
                else -> UNKNOWN
            }
        }
    }
}