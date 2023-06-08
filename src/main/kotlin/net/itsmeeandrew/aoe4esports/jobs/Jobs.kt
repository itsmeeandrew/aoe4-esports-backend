package net.itsmeeandrew.aoe4esports.jobs

import jakarta.annotation.PostConstruct
import net.itsmeeandrew.aoe4esports.client.LiquipediaClient
import net.itsmeeandrew.aoe4esports.common.*
import net.itsmeeandrew.aoe4esports.common.`interface`.IParseHandler
import net.itsmeeandrew.aoe4esports.model.Match
import net.itsmeeandrew.aoe4esports.model.Series
import net.itsmeeandrew.aoe4esports.model.Tournament
import net.itsmeeandrew.aoe4esports.model.TournamentRound
import net.itsmeeandrew.aoe4esports.service.MatchService
import net.itsmeeandrew.aoe4esports.service.SeriesService
import net.itsmeeandrew.aoe4esports.service.TournamentRoundService
import net.itsmeeandrew.aoe4esports.service.TournamentService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
private class Jobs(
    private val liquipediaClient: LiquipediaClient,
    private val matchService: MatchService,
    private val seriesService: SeriesService,
    private val tournamentRoundService: TournamentRoundService,
    private val tournamentService: TournamentService
) {

    @PostConstruct
    private fun init() {

    }

    private fun parse(parseHandler: IParseHandler) {
        timeout(35)

        parseHandler.getTournamentIds().forEach { tId ->
            val tournamentParser = liquipediaClient.getTournamentParser(tId)
            val tournament = tournamentParser.parseTournament()

            if (tournament.format == TournamentFormat.ONE_VS_ONE) {
                parseHandler.handleTournament(tournament)
                val tournamentRounds = tournamentParser.parseTournamentRounds()

                tournamentRounds.forEach { tr ->
                    parseHandler.handleTournamentRound(tr)

                    val tournamentRoundParser = liquipediaClient.getTournamentRoundParser(tr)
                    val seriesAndMatches = tournamentRoundParser.parseSeriesAndMatches()

                    seriesAndMatches.forEach { (series, matches) ->
                        parseHandler.handleSeriesAndMatches(series, matches)
                    }
                }
            }
        }
    }

    @Scheduled(cron = "30 1 1,15 * *") // Every 2 weeks on the 1st and the 15th of every month at 1:30 AM
    private fun addNewTournaments() {
        parse(AddNewTournamentParseHandler())
    }

    @Scheduled(cron = "0 */6 * * *") // Every six hours
    private fun updateOngoingTournaments() {
        parse(UpdateOngoingTournamentParseHandler())
    }

    @Scheduled(cron = "0 0 * * 0") // Every week at 00:00 on Sunday
    private fun updateUpcomigTournaments() {
        parse(UpdateUpcomingTournamentParseHandler())
    }

    inner class UpdateOngoingTournamentParseHandler: IParseHandler {
        override fun getTournamentIds(): List<String> {
            return tournamentService.findAll()
                .filter { t -> isTournamentOngoing(t) }
                .map { t -> t.id}
        }

        override fun handleTournament(tournament: Tournament) {}

        override fun handleTournamentRound(tournamentRound: TournamentRound) {
            addTournamentRound(tournamentRound)
        }

        override fun handleSeriesAndMatches(series: Series, matches: List<Match>) {
            val existingSeries = seriesService.find(series)
            if (existingSeries == null) {
                val createdSeries = addSeries(series)
                if (createdSeries != null) {
                    matches.forEach { m ->
                        addMatch(m.copy(seriesId = createdSeries.id))
                    }
                }
            } else {
                val matchesInSeries = matchService.findBySeriesId(existingSeries.id!!)
                if (matchesInSeries.size != matches.size) {
                    println("New matches found for Series.")
                    seriesService.updateScores(existingSeries.id, series.homeScore, series.awayScore)
                    matchService.deleteMany(matchesInSeries)
                    matches.forEach { m ->
                        addMatch(m.copy(seriesId = existingSeries.id))
                    }
                }
            }
        }
    }

    inner class AddNewTournamentParseHandler: IParseHandler {
        override fun getTournamentIds(): List<String> {
            val existingTournamentsIds = tournamentService.findAll().map { t -> t.id }.toSet()
            val tournamentIds = liquipediaClient.getTournamentIds(TournamentTier.S).toSet()

            return tournamentIds.subtract(existingTournamentsIds).toList()
        }

        override fun handleTournament(tournament: Tournament) {
            addTournament(tournament)
        }

        override fun handleTournamentRound(tournamentRound: TournamentRound) {
            addTournamentRound(tournamentRound)
        }

        override fun handleSeriesAndMatches(series: Series, matches: List<Match>) {
            val createdSeries = addSeries(series)
            matches.forEach { match ->
                addMatch(match.copy(seriesId = createdSeries?.id))
            }
        }
    }

    inner class UpdateUpcomingTournamentParseHandler: IParseHandler {
        override fun getTournamentIds(): List<String> {
            return tournamentService.findAll()
                .filter { t -> isTournamentUpcoming(t) }
                .map { t -> t.id }
        }

        override fun handleTournament(tournament: Tournament) {}

        override fun handleTournamentRound(tournamentRound: TournamentRound) {
            addTournamentRound(tournamentRound)
        }

        override fun handleSeriesAndMatches(series: Series, matches: List<Match>) {
            val existingSeries = seriesService.find(series)
            if (existingSeries == null) {
                addSeries(series)
            } else {
                if (existingSeries.date == null && series.date != null) {
                    seriesService.updateDate(existingSeries.id!!, series.date)
                }

                if (existingSeries.time == null && series.time != null) {
                    seriesService.updateTime(existingSeries.id!!, series.time)
                }
            }
        }
    }

    private fun addTournament(tournament: Tournament) {
        val createdTournament = tournamentService.create(tournament)
        if (createdTournament != null) {
            println("[TOURNAMENT] Added tournament ${tournament.name}.")
        }
    }

    private fun addTournamentRound(tournamentRound: TournamentRound) {
        val createdTournamentRound = tournamentRoundService.create(tournamentRound)
        if (createdTournamentRound != null) {
            println("[TOURNAMENT ROUND] Added tournament round ${tournamentRound.name}.")
        }
    }

    private fun addSeries(series: Series): Series? {
        return seriesService.create(series)
    }

    private fun addMatch(match: Match) {
        val createdMatch = matchService.create(match)
        if (createdMatch != null) {
            println("[MATCH] Added match.")
        }
    }
}