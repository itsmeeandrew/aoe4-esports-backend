package net.itsmeeandrew.aoe4esports.jobs

import jakarta.annotation.PostConstruct
import net.itsmeeandrew.aoe4esports.client.LiquipediaClient
import net.itsmeeandrew.aoe4esports.model.Series
import net.itsmeeandrew.aoe4esports.model.Tournament
import net.itsmeeandrew.aoe4esports.model.TournamentRound
import net.itsmeeandrew.aoe4esports.service.SeriesService
import net.itsmeeandrew.aoe4esports.service.TournamentRoundService
import net.itsmeeandrew.aoe4esports.service.TournamentService
import org.springframework.stereotype.Component

@Component
class Jobs(
    private val liquipediaClient: LiquipediaClient,
    private val seriesService: SeriesService,
    private val tournamentService: TournamentService,
    private val tournamentRoundService: TournamentRoundService,
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
            val series = tournamentRoundParser.parseSeries()
            series.forEach { s ->
                addSeries(s)
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

    private fun addSeries(series: Series) {
        val createdSeries = seriesService.createSeries(series)
        if (createdSeries != null) {
            println("Added series: [${createdSeries.id}]")
        }
    }
}