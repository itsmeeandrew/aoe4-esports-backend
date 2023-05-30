package net.itsmeeandrew.aoe4esports.client.selector

import net.itsmeeandrew.aoe4esports.common.`interface`.ISelector
import org.jsoup.nodes.Element

class BracketSelector (
    private val playerSelectorPrefix: String
): ISelector {
    override val container = ".bracket-column.bracket-column-matches"
    override fun containerFilter (e: Element) = e.select(".bracket-header").isNotEmpty() && e.select(".bracket-game ${playerSelectorPrefix}-top").isNotEmpty()
    override val header = ".bracket-header"
    override fun uniqueHeader(e: Element): String? {
        val previousElement = e.previousElementSibling()
        return if (previousElement == null) {
            null
        } else {
            if (!previousElement.hasClass("bracket-game") &&
                previousElement.select("abbr").firstOrNull()?.attr("title")?.startsWith("Best of") == true &&
                previousElement.select(".bracket-header").isEmpty()) {
                previousElement.text()
            } else null
        }
    }
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