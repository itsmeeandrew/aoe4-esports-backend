package net.itsmeeandrew.aoe4esports.client.selector

import net.itsmeeandrew.aoe4esports.common.`interface`.ISelector
import org.jsoup.nodes.Element

class GroupSelector: ISelector {
    override val container = ".template-box"
    override fun containerFilter (e: Element) = e.select(".wikitable").isNotEmpty() && e.select(".matchlist").isNotEmpty()
    override val header = "div table.grouptable tbody tr:first-child th span"
    override fun uniqueHeader(e: Element) = null
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