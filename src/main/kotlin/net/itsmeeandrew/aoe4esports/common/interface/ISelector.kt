package net.itsmeeandrew.aoe4esports.common.`interface`

import org.jsoup.nodes.Element

interface ISelector {
    val container: String
    fun containerFilter(e: Element): Boolean
    val header: String
    fun uniqueHeader(e: Element): String?
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