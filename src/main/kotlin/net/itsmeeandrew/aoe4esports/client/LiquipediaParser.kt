package net.itsmeeandrew.aoe4esports.client

import net.itsmeeandrew.aoe4esports.client.selector.BracketSelector
import net.itsmeeandrew.aoe4esports.client.selector.GroupSelector
import net.itsmeeandrew.aoe4esports.common.*
import net.itsmeeandrew.aoe4esports.common.`interface`.ISelector
import net.itsmeeandrew.aoe4esports.model.*
import net.itsmeeandrew.aoe4esports.service.CivilizationService
import net.itsmeeandrew.aoe4esports.service.MapService
import net.itsmeeandrew.aoe4esports.service.PlayerService
import net.itsmeeandrew.aoe4esports.service.TournamentRoundPhaseService
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
    private val tournamentRoundPhaseService: TournamentRoundPhaseService
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

        private fun getBracketPlayerSelectorPrefix() =
            if (root.select(".bracket-player-top").isEmpty()) ".bracket-team"
            else ".bracket-player"

        private fun getDateAndTime(seriesElement: Element, dateElementSelector: String): Pair<LocalDate?, LocalTime?> {
            val dateElement = seriesElement.select(dateElementSelector).first()

            if (dateElement != null) {
                if (isFullDate(dateElement)) {
                    val secondsSinceEpoch = dateElement.attr("data-timestamp").toLong()
                    val timezoneId = dateElement.select("span abbr").text()
                    val instant = Instant.ofEpochSecond(secondsSinceEpoch)

                    var zoneId: ZoneId? = null
                    try {
                        zoneId = ZoneId.of(timezoneId)
                    } catch (e: Exception) {
                        if (timezoneId == "BOT") {
                            zoneId = ZoneId.of("America/La_Paz")
                        }
                    }

                    return if (zoneId != null) {
                        val dateTimeInUTC = ZonedDateTime.ofInstant(instant, zoneId).withZoneSameInstant(ZoneOffset.UTC)

                        Pair(dateTimeInUTC.toLocalDate(), dateTimeInUTC.toLocalTime())
                    } else {
                        Pair(null, null)
                    }
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

        private fun getMatchMapId(matchElement: Element, mapSelector: String): Int? {
            val mapName = matchElement.select(mapSelector).getOwnText()
            return if (mapName != null) {
                mapService.findByNameOrCreate(mapName)?.id
            } else null
        }

        private fun getMatchWinnerPlayerId(matchElement: Element, homePlayerId: Int?, awayPlayerId: Int?): Int? {
            return try {
                val isHomePlayerWinner = matchElement.select("div > div > i")[0].classNames().contains("forest-green-text")
                val isAwayPlayerWinner = matchElement.select("div > div > i")[1].classNames().contains("forest-green-text")

                when {
                    isHomePlayerWinner -> homePlayerId
                    isAwayPlayerWinner -> awayPlayerId
                    else -> null
                }
            } catch (e: Exception) {
                null
            }
        }

        private fun calculateBestOf(homeScore: Int?, awayScore: Int?): Int? {
            return if (homeScore != null && awayScore != null) {
                homeScore.coerceAtLeast(awayScore) * 2 - 1
            } else null
        }

        private fun getTournamentRoundPhase(containerElement: Element, containerSelector: String, defaultHeaderText: String): TournamentRoundPhase? {
            val phaseName = containerElement.select(containerSelector).getText() ?: defaultHeaderText
            return tournamentRoundPhaseService.findOrCreate(
                TournamentRoundPhase(
                    id = null,
                    name = phaseName,
                    bestOf = null,
                    tournamentRoundId = tournamentRound.id
                )
            )
        }

        private fun getSeriesPlayerIds(seriesElement: Element, homePlayerSelector: String, awayPlayerSelector: String): Pair<Int?, Int?> {
            val homePlayerName = seriesElement.select(homePlayerSelector).getOwnText()
            val awayPlayerName = seriesElement.select(awayPlayerSelector).getOwnText()

            return if (homePlayerName != null && awayPlayerName != null) {
                Pair(
                    playerService.findByNameOrCreate(homePlayerName)?.id,
                    playerService.findByNameOrCreate(awayPlayerName)?.id
                )
            } else {
                Pair(null, null)
            }
        }

        private fun getSeriesScores(seriesElement: Element, homeScoreSelector: String, awayScoreSelector: String): Pair<Int?, Int?> {
            val homeScoreString = seriesElement.select(homeScoreSelector).getOwnText()
            val awayScoreString = seriesElement.select(awayScoreSelector).getText()

            return if (homeScoreString == "W" && awayScoreString == "FF") {
                Pair(0, -1)
            }
            else if (homeScoreString == "FF" && awayScoreString == "W") {
                Pair(-1, 0)
            }
            else {
                Pair(
                    homeScoreString?.toIntOrNull(),
                    awayScoreString?.toIntOrNull()
                )
            }
        }

        private fun getMatchCivilizationIds(matchElement: Element, homeCivilizationSelector: String, awayCivilizationSelector: String): Pair<Int?, Int?> {
            return try {
                val homeCivilizationName = matchElement.select(homeCivilizationSelector)[0].attr("title")
                val awayCivilizationName = matchElement.select(awayCivilizationSelector)[1].attr("title")

                val homeCivilizationId = civilizationService.findByName(homeCivilizationName)?.id
                val awayCivilizationId = civilizationService.findByName(awayCivilizationName)?.id

                Pair(homeCivilizationId, awayCivilizationId)
            } catch (e: Exception) {
                Pair(null, null)
            }
        }

        fun parseSeriesAndMatches(): List<Pair<Series, List<Match>>> {
            return getSeriesAndMatches(BracketSelector(getBracketPlayerSelectorPrefix())) + getSeriesAndMatches(GroupSelector())
        }

        private fun getSeriesAndMatches(selector: ISelector): List<Pair<Series, List<Match>>> {
            val listOfPairs = mutableListOf<Pair<Series, List<Match>>>()

            root.select(selector.container)
                .filter { container -> selector.containerFilter(container) }
                .forEach { container ->
                    val tournamentRoundPhase = getTournamentRoundPhase(container, selector.header, selector.defaultHeaderText)
                    val bestOfList = mutableListOf<Int>()

                    container.select(selector.series)
                        .filter { series -> selector.seriesFilter(series) }
                        .forEach { series ->
                            val (homePlayerId, awayPlayerId) = getSeriesPlayerIds(series, selector.homePlayer, selector.awayPlayer)
                            var (homeScore, awayScore) = getSeriesScores(series, selector.homeScore, selector.awayScore)
                            val (date, time) = getDateAndTime(series, selector.dateElement)

                            val bestOf = calculateBestOf(homeScore, awayScore)
                            val uniqueRoundPhaseName = selector.uniqueHeader(series)
                            var uniqueRoundPhase: TournamentRoundPhase? = null

                            if (uniqueRoundPhaseName != null) {
                                uniqueRoundPhase = tournamentRoundPhaseService.create(
                                    TournamentRoundPhase(
                                        id = null,
                                        name = uniqueRoundPhaseName,
                                        bestOf = bestOf,
                                        tournamentRoundId = tournamentRound.id
                                    )
                                )
                            }
                            else if (homeScore != -1 && awayScore != -1 && bestOf != null) {
                                bestOfList.add(bestOf)
                            }

                            if (isDateTimeUpcoming(date, time)) {
                                homeScore = 0
                                awayScore = 0
                            }

                            val seriesMatchesList = mutableListOf<Match>()
                            val seriesMatches = series.select(selector.matches)
                            seriesMatches.forEach { match ->
                                val (homeCivilizationId, awayCivilizationId) = getMatchCivilizationIds(match, selector.homeCivilization, selector.awayCivilization)
                                val mapId = getMatchMapId(match, selector.map)
                                val winnerPlayerId = getMatchWinnerPlayerId(match, homePlayerId, awayPlayerId)

                                if (winnerPlayerId != null) {
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
                                } else if (homeCivilizationId != null && awayCivilizationId != null && mapId != null) {
                                    println("[MATCH ERROR]: Missing winner player ID. (homePlayerId: $homePlayerId awayPlayerId: $awayPlayerId)")
                                }
                            }

                            if (homePlayerId != null && awayPlayerId != null && homeScore != null && awayScore != null && tournamentRoundPhase?.id != null) {
                                val newSeries = Series(
                                    awayPlayerId = awayPlayerId,
                                    awayScore = awayScore,
                                    date = date,
                                    id = null,
                                    homePlayerId = homePlayerId,
                                    homeScore = homeScore,
                                    time = time,
                                    tournamentRoundId = tournamentRound.id,
                                    tournamentRoundPhaseId = uniqueRoundPhase?.id ?: tournamentRoundPhase.id
                                )
                                listOfPairs.add(Pair(newSeries, seriesMatchesList))
                            } else {
                                println("[SERIES ERROR]: Missing information. (home: $homePlayerId away:$awayPlayerId $homeScore - $awayScore)")
                            }
                    }

                    val bestOfSet = bestOfList.toSet()
                    if (bestOfSet.size == 1 && tournamentRoundPhase?.id != null) {
                        tournamentRoundPhaseService.updateBestOf(tournamentRoundPhase.id, bestOfSet.elementAt(0))
                    }
                    else if (tournamentRoundPhase?.id == null) {
                        println("Could not update tournament round phase best of value, because it's null.")
                    }
                    else {
                        println("Could not determine bestOf value for tournament round phase. Number of best of values found: ${bestOfSet.size}")
                    }
            }

            return listOfPairs
        }
    }
}