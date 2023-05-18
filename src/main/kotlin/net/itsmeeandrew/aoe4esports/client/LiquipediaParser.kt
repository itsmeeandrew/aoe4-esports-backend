package net.itsmeeandrew.aoe4esports.client

import net.itsmeeandrew.aoe4esports.model.Match
import net.itsmeeandrew.aoe4esports.model.Series
import net.itsmeeandrew.aoe4esports.model.Tournament
import net.itsmeeandrew.aoe4esports.model.TournamentRound
import net.itsmeeandrew.aoe4esports.service.CivilizationService
import net.itsmeeandrew.aoe4esports.service.MapService
import net.itsmeeandrew.aoe4esports.service.PlayerService
import net.itsmeeandrew.aoe4esports.util.TournamentFormat
import net.itsmeeandrew.aoe4esports.util.TournamentTier
import net.itsmeeandrew.aoe4esports.util.getText
import net.itsmeeandrew.aoe4esports.util.liquipediaDateFormat
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component
import java.time.*

@Component
class LiquipediaParser(
    private val civilizationService: CivilizationService,
    private val mapService: MapService,
    private val playerService: PlayerService,
) {

    private fun cleanAndParseHtmlString(htmlString: String): Document {
        val cleanedHtmlString = htmlString.replace("\\", "")

        return Jsoup.parse(cleanedHtmlString)
    }

    fun getTournamentIds(htmlString: String): List<String> {
        val root = cleanAndParseHtmlString(htmlString)
        val selector = ".divRow .divCell.Tournament.Header b a"
        val tournamentLinkElements = root.select(selector)

        val tournamentIds = tournamentLinkElements.map { element ->
            element.attr("title").replace(" ", "_")
        }

        return tournamentIds
    }

    inner class TournamentParser(
        private val htmlString: String,
        private val tournamentId: String,
        private val logoUrl: String, // TODO: Can be retrieved from htmlString
        private val name: String // TODO: Can be retrieved from htmlString
    ) {
        private val root: Document = cleanAndParseHtmlString(this.htmlString)

        fun parseTournament(): Tournament {
            val tournamentInfoElement = root.select(".fo-nttax-infobox").first()

            val tournamentInfoMap = tournamentInfoElement
                ?.select("> div")
                ?.filter { e -> e.childNodes().size == 2 }
                ?.mapNotNull { e ->
                    val infoKey = e.firstElementChild()?.text()
                    val infoValue = e.lastElementChild()?.text()

                    if (!infoKey.isNullOrBlank() && !infoValue.isNullOrBlank()) {
                        infoKey.trim().dropLast(1) to infoValue.trim()
                    } else null
                }?.toMap() ?: emptyMap()

            val twitchUrl = tournamentInfoElement?.select("a i.lp-twitch")?.first()?.parent()?.attr("href") ?: ""
            val startDate = LocalDate.parse(tournamentInfoMap["Start Date"])
            val endDate = LocalDate.parse(tournamentInfoMap["End Date"])

            val tierText = tournamentInfoMap["Liquipedia Tier"]
            val tier = when (tierText?.get(0)) {
                'S' -> TournamentTier.S
                'A' -> TournamentTier.A
                else -> TournamentTier.UNKNOWN
            }

            val formatText = tournamentInfoMap["Format"] ?: ""
            val format: TournamentFormat = when {
                formatText.contains("1v1") -> TournamentFormat.ONE_VS_ONE
                formatText.contains("/2v2|3v3|4v4|team/gm") -> TournamentFormat.TEAM
                formatText.contains("FFA") -> TournamentFormat.FFA
                else -> TournamentFormat.UNKNOWN
            }

            return Tournament(endDate, format, tournamentId, logoUrl, name, startDate, tier, twitchUrl)
        }

        fun parseTournamentRounds(): List<TournamentRound> {
            val tournamentRounds: MutableList<TournamentRound> = root.select(".tabs-static ul.tabs li a").mapNotNull { element ->
                val id = element.attr("title")
                val name = element.text()

                if (id.isNotEmpty() && !name.startsWith("Age of Empires")) {
                    TournamentRound(id, name, tournamentId)
                } else null
            }.toMutableList()

            tournamentRounds.add(TournamentRound(
                tournamentId,
                "Main Event",
                tournamentId
            ))

            return tournamentRounds
        }
    }

    inner class TournamentRoundParser(
        htmlString: String,
        private val tournamentRound: TournamentRound
    ) {
        private val root: Document = cleanAndParseHtmlString(htmlString)

        private fun isValidBracketGame(seriesElement: Element) =
            seriesElement.select(".bracket-player-middle").isEmpty() && seriesElement.select(".timer-object").isNotEmpty()

        private fun getPlayerSelectorPrefix(seriesElement: Element) =
            if (seriesElement.select(".bracket-player-top").isEmpty()) ".bracket-team"
            else ".bracket-player"

        private fun getDateAndTime(dateElement: Element?): Pair<LocalDate?, LocalTime?> {
            if (dateElement != null) {
                if (isFullDate(dateElement)) {
                    val secondsSinceEpoch = dateElement.attr("data-timestamp").toLong()
                    val timezoneId = dateElement.select("span abbr").text()
                    val instant = Instant.ofEpochSecond(secondsSinceEpoch)

                    val dateTimeInUTC = ZonedDateTime.ofInstant(instant, ZoneId.of(timezoneId)).withZoneSameInstant(ZoneOffset.UTC)

                    return Pair(dateTimeInUTC.toLocalDate(), dateTimeInUTC.toLocalTime())
                }
                else if (isPartialDate(dateElement)) {
                    val dateString = dateElement.text()
                    val date = LocalDate.parse(dateString, liquipediaDateFormat)

                    return Pair(date, null)
                }
                else {
                    return Pair(null, null)
                }
            } else {
                return Pair(null, null)
            }
        }

        private fun isFullDate(dateElement: Element) =
            dateElement.attr("data-timestamp").toLongOrNull() != null

        private fun isPartialDate(dateElement: Element): Boolean {
            if (isFullDate(dateElement)) {
                return true
            }

            val dateString = dateElement.text()
            return try {
                LocalDate.parse(dateString, liquipediaDateFormat)
                true
            } catch (e: Exception) {
                false
            }
        }

        private fun getCivilizationIdByName(name: String): Int? {
            return civilizationService.findByName(name)?.id
        }

        fun parseSeriesAndMatches(): List<Pair<Series, List<Match>>> {
            return parseBracketSeriesAndMatches() + parseGroupSeriesAndMatches()
        }

        // TODO: parseGroupSeriesAndMatch & parseBracketSeriesAndMatches have similar logic, only the rules differs. Perhaps there is way to handle them in one function.
        private fun parseGroupSeriesAndMatches(): MutableList<Pair<Series, List<Match>>> {
           val listOfPairs = mutableListOf<Pair<Series, List<Match>>>()

           root.select(".template-box")
               .filter { gbe ->
                   gbe.select(".wikitable").isNotEmpty() && gbe.select(".matchlist").isNotEmpty()
               }
               .forEach { gbe ->
                   val groupNameElement = gbe.select("div table.grouptable tbody tr:first-child th span").firstOrNull()
                   val groupName = groupNameElement?.text() ?: "Group Stage"

                   gbe.select("tr.match-row")
                       .forEach { series ->
                           val homePlayerName = series.select("td:first-child").getText()
                           val homeScore = series.select("td:nth-child(2)").firstOrNull()?.ownText()?.trim()?.toInt()
                           val awayPlayerName = series.select("td:nth-child(4)").getText()
                           val awayScore = series.select("td:nth-child(3)").getText().toInt()

                           val dateElement = series.select("td:nth-child(2) .timer-object").firstOrNull()
                           val (date, time) = getDateAndTime(dateElement)

                           val bestOf = if (homeScore != null) homeScore.coerceAtLeast(awayScore) * 2 - 1 else 0

                           val homePlayerId = playerService.findByNameOrCreate(homePlayerName)?.id
                           val awayPlayerId = playerService.findByNameOrCreate(awayPlayerName)?.id

                           val groupMatchesList = mutableListOf<Match>()
                           val groupMatches = root.select("td:nth-child(2) .bracket-popup-body .bracket-popup-body-match")
                           groupMatches.forEach { match ->
                               val homeCivilizationName = match.select("div .draft a")[0].attr("title")
                               val awayCivilizationName = match.select("div .draft a")[1].attr("title")
                               val mapName = match.select("> div > a").firstOrNull()?.ownText()

                               val isHomePlayerWinner = match.select("div > div > i").firstOrNull()?.classNames()?.contains("forest-green-text") ?: false
                               val isAwayPlayerWinner = match.select("div > div > i")[1].classNames().contains("forest-green-text")

                               val homeCivilizationId = getCivilizationIdByName(homeCivilizationName)
                               val awayCivilizationId = getCivilizationIdByName(awayCivilizationName)
                               val mapId = if (mapName != null) mapService.findByNameOrCreate(mapName)?.id else null

                               val winnerPlayerId = when {
                                   isHomePlayerWinner -> homePlayerId
                                   isAwayPlayerWinner -> awayPlayerId
                                   else -> null
                               }

                               if (homeCivilizationId != null && awayCivilizationId != null) {
                                   groupMatchesList.add(
                                       Match(
                                           awayCivilizationId = awayCivilizationId,
                                           homeCivilizationId = homeCivilizationId,
                                           id = null,
                                           mapId = mapId,
                                           seriesId = null,
                                           winnerPlayerId = winnerPlayerId
                                       )
                                   )
                               } else {
                                   println("Skipping group match because of missing information.")
                               }
                           }

                           if (homePlayerId != null && awayPlayerId != null && homeScore != null) {
                               val newSeries = Series(
                                   awayPlayerId = awayPlayerId,
                                   awayScore = awayScore,
                                   bestOf = bestOf,
                                   bracketRound = groupName,
                                   date = date,
                                   id = null,
                                   homePlayerId = homePlayerId,
                                   homeScore = homeScore,
                                   time = time,
                                   tournamentRoundId = tournamentRound.id
                               )
                               listOfPairs.add(Pair(newSeries, groupMatchesList))
                           } else {
                               println("Skipping group Series because of missing Id")
                           }
                       }
               }
           return listOfPairs
        }

        private fun parseBracketSeriesAndMatches(): List<Pair<Series, List<Match>>> {
            val listOfPairs = mutableListOf<Pair<Series, List<Match>>>()

            root.select(".bracket-column.bracket-column-matches")
                .filter { bce -> bce.select(".bracket-header").isNotEmpty() }
                .forEach { bce ->
                    val bracketHeaderText = bce.select(".bracket-header").getText().split("(")
                    val bracketRound = bracketHeaderText[0]
                    val bracketBestOf = if (bracketHeaderText.size == 2) bracketHeaderText[1].replace("[^0-9]".toRegex(), "") else ""

                    bce.select(".bracket-game")
                        .filter { series -> isValidBracketGame(series) }
                        .mapNotNull { series ->
                            val playerSelectorPrefix = getPlayerSelectorPrefix(series)

                            val homePlayerName = series.select("${playerSelectorPrefix}-top span:nth-child(2)").getText()
                            val awayPlayerName = series.select("${playerSelectorPrefix}-bottom span:nth-child(2)").getText()
                            val homeScore = series.select("${playerSelectorPrefix}-top .bracket-score").getText()
                            val awayScore = series.select("${playerSelectorPrefix}-bottom .bracket-score").getText()

                            val dateElement = series.select(".timer-object").first()
                            val (date, time) = getDateAndTime(dateElement)

                            val homePlayerId = playerService.findByNameOrCreate(homePlayerName)?.id
                            val awayPlayerId = playerService.findByNameOrCreate(awayPlayerName)?.id

                            val seriesMatchesList = mutableListOf<Match>()
                            val seriesMatches = series.select(".bracket-popup-body .bracket-popup-body-match")
                            seriesMatches.forEach { match ->
                                val homeCivilizationName = match.select("div .draft a")[0].attr("title")
                                val awayCivilizationName = match.select("div .draft a")[1].attr("title")
                                val mapName = match.select("> div > a").firstOrNull()?.ownText()

                                val isHomePlayerWinner = match.select("div > div > i").firstOrNull()?.classNames()?.contains("forest-green-text") ?: false
                                val isAwayPlayerWinner = match.select("div > div > i")[1].classNames().contains("forest-green-text")

                                val homeCivilizationId = getCivilizationIdByName(homeCivilizationName)
                                val awayCivilizationId = getCivilizationIdByName(awayCivilizationName)
                                val mapId = if (mapName != null) mapService.findByNameOrCreate(mapName)?.id else null

                                val winnerPlayerId = when {
                                    isHomePlayerWinner -> homePlayerId
                                    isAwayPlayerWinner -> awayPlayerId
                                    else -> null
                                }

                                if (homeCivilizationId != null && awayCivilizationId != null) {
                                    seriesMatchesList.add(
                                        Match(
                                            awayCivilizationId = awayCivilizationId,
                                            homeCivilizationId = homeCivilizationId,
                                            id = null,
                                            mapId = mapId,
                                            seriesId = null,
                                            winnerPlayerId = winnerPlayerId
                                        )
                                    )
                                } else {
                                    println("Skipping match because of missing information.")
                                }
                            }

                            if (homePlayerId != null && awayPlayerId != null) {
                                val newSeries = Series(
                                    awayPlayerId = awayPlayerId,
                                    awayScore = awayScore.toInt(),
                                    bestOf = bracketBestOf.toInt(),
                                    bracketRound = bracketRound,
                                    date = date,
                                    id = null,
                                    homePlayerId = homePlayerId,
                                    homeScore = homeScore.toInt(),
                                    time = time,
                                    tournamentRoundId = tournamentRound.id
                                )
                                listOfPairs.add(Pair(newSeries, seriesMatchesList))
                            } else {
                                println("Skipping bracket Series because of missing ID.")
                            }
                    }
            }

            return listOfPairs
        }
    }
}