package net.itsmeeandrew.aoe4esports.common

import org.jsoup.nodes.Element
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

    override fun toString(): String {
        return formatName
    }
}

interface Selector {
    val container: String
    fun containerFilter(e: Element): Boolean
    val header: String
    val defaultHeaderText: String
    val series: String
    fun seriesFilter(e: Element): Boolean
    val homePlayer: String
    val awayPlayer: String
    val homeScore: String
    val awayScore: String
    val dateElement: String
    val matches: String
    val homeCivilization: String
    val awayCivilization: String
    val homePlayerWinner: String
    val awayPlayerWinner: String
    val map: String
}

class BracketSelector (
    playerSelectorPrefix: String
): Selector {
    override val container = ".bracket-column.bracket-column-matches"
    override fun containerFilter (e: Element) = e.select(".bracket-header").isNotEmpty()
    override val header = ".bracket-header"
    override val defaultHeaderText = "Bracket Stage"
    override val series = ".bracket-game"
    override fun seriesFilter(e: Element) = e.select(".bracket-player-middle").isEmpty() && e.select(".timer-object").isNotEmpty()
    override val homePlayer = "${playerSelectorPrefix}-top span:nth-child(2)"
    override val awayPlayer = "${playerSelectorPrefix}-bottom span:nth-child(2)"
    override val homeScore = "${playerSelectorPrefix}-top .bracket-score"
    override val awayScore = "${playerSelectorPrefix}-bottom .bracket-score"
    override val dateElement = ".timer-object"
    override val matches = ".bracket-popup-body .bracket-popup-body-match"
    override val homeCivilization = "div .draft a"
    override val awayCivilization = "div .draft a"
    override val homePlayerWinner = "div > div > i"
    override val awayPlayerWinner = "div > div > i"
    override val map = "> div > a"
}

class GroupSelector: Selector {
    override val container = ".template-box"
    override fun containerFilter (e: Element) = e.select(".wikitable").isNotEmpty() && e.select(".matchlist").isNotEmpty()
    override val header = "div table.grouptable tbody tr:first-child th span"
    override val defaultHeaderText = "Group Stage"
    override val series = "tr.match-row"
    override fun seriesFilter(e: Element) = true
    override val homePlayer = "td:first-child span:first-child"
    override val awayPlayer = "td:nth-child(4) span:nth-child(2)"
    override val homeScore = "td:nth-child(2)"
    override val awayScore = "td:nth-child(3)"
    override val dateElement = "td:nth-child(2) .timer-object"
    override val matches = "td:nth-child(2) .bracket-popup-body .bracket-popup-body-match"
    override val homeCivilization = "div .draft a"
    override val awayCivilization = "div .draft a"
    override val homePlayerWinner = "div > div > i"
    override val awayPlayerWinner = "div > div > i"
    override val map = "> div > a"
}