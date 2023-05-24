package net.itsmeeandrew.aoe4esports.jobs

import jakarta.annotation.PostConstruct
import net.itsmeeandrew.aoe4esports.client.LiquipediaClient
import net.itsmeeandrew.aoe4esports.common.TournamentFormat
import net.itsmeeandrew.aoe4esports.common.timeout
import net.itsmeeandrew.aoe4esports.model.Match
import net.itsmeeandrew.aoe4esports.model.Series
import net.itsmeeandrew.aoe4esports.model.Tournament
import net.itsmeeandrew.aoe4esports.model.TournamentRound
import net.itsmeeandrew.aoe4esports.service.MatchService
import net.itsmeeandrew.aoe4esports.service.SeriesService
import net.itsmeeandrew.aoe4esports.service.TournamentRoundService
import net.itsmeeandrew.aoe4esports.service.TournamentService
import org.springframework.stereotype.Component

@Component
class Jobs(
    private val liquipediaClient: LiquipediaClient,
    private val matchService: MatchService,
    private val seriesService: SeriesService,
    private val tournamentRoundService: TournamentRoundService,
    private val tournamentService: TournamentService
) {

    @PostConstruct
    fun init() {
        timeout(35)

        //val sTierTournamentIds = liquipediaClient.getTournamentIds(TournamentTier.S)
        listOf("Golden_League/1").forEach { tid ->
            val tournamentParser = liquipediaClient.getTournamentParser(tid)
            val tournament = tournamentParser.parseTournament()

            if (tournament.format == TournamentFormat.ONE_VS_ONE) {
                addTournament(tournament)
                val tournamentRounds = tournamentParser.parseTournamentRounds()

                tournamentRounds.forEach { tr ->
                    addTournamentRound(tr)

                    val tournamentRoundParser = liquipediaClient.getTournamentRoundParser(tr)
                    val seriesAndMatches = tournamentRoundParser.parseSeriesAndMatches()
                    seriesAndMatches.forEach { (series, matches) ->
                        val existingSeries = seriesService.find(series)
                        if (existingSeries == null) {
                            println("[TOURNAMENT ROUND] ${tr.name} [SERIES] Added series.")
                            val createdSeries = addSeries(series)
                            matches.forEach { match ->
                                addMatch(match.copy(seriesId = createdSeries?.id))
                            }
                        } else {
                            println("[TOURNAMENT ROUND] ${tr.name} [SERIES] Series already exists.")
                        }
                    }
                }
            } else {
                println("[TOURNAMENT] ${tournament.name} has unsupported format. (${tournament.format.name})")
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