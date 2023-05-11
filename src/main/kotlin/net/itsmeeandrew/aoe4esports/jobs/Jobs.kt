package net.itsmeeandrew.aoe4esports.jobs

import jakarta.annotation.PostConstruct
import net.itsmeeandrew.aoe4esports.client.LiquipediaClient
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
        val tournamentParser = liquipediaClient.getTournamentParser("GENESIS")
        val tournament = tournamentParser.parseTournament()
        addTournament(tournament)

        val tournamentRounds = tournamentParser.parseTournamentRounds()

        tournamentRounds.forEach { tr ->
            addTournamentRound(tr)

            val tournamentRoundParser = liquipediaClient.getTournamentRoundParser(tr)
            val seriesAndMatches = tournamentRoundParser.parseSeriesAndMatches()
            seriesAndMatches.forEach { seriesAndMatchesPair ->
                val (series, matches) = seriesAndMatchesPair
                val existingSeries = seriesService.findByDetails(series)
                if (existingSeries == null) {
                    println("Series created for matches.")
                    val createdSeries = addSeries(series)
                    matches.forEach { match ->
                        addMatch(match.copy(seriesId = createdSeries?.id))
                    }
                } else {
                    println("Series already exists for matches.")
                    matches.forEach { match ->
                        addMatch(match.copy(seriesId = existingSeries.id))
                    }
                }
            }
        }
    }

    private fun addTournament(tournament: Tournament) {
        val createdTournament = tournamentService.createTournament(tournament)
        if (createdTournament != null) {
            println("Added tournament: ${createdTournament.name}")
        }
    }

    private fun addTournamentRound(tournamentRound: TournamentRound) {
        val createdTournamentRound = tournamentRoundService.createTournamentRound(tournamentRound)
        if (createdTournamentRound != null) {
            println("Added tournament round: ${createdTournamentRound.name}")
        }
    }

    private fun addSeries(series: Series): Series? {
        val createdSeries = seriesService.createSeries(series)
        return if (createdSeries != null) {
            println("Added series: [${createdSeries.id}]")
            createdSeries
        } else null
    }

    private fun addMatch(match: Match) {
        val createdMatch = matchService.create(match)
        if (createdMatch != null) {
            println("Added match: ${createdMatch.id}")
        }
    }
}