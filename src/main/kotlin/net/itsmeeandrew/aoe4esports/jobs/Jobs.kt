package net.itsmeeandrew.aoe4esports.jobs

import jakarta.annotation.PostConstruct
import net.itsmeeandrew.aoe4esports.client.LiquipediaClient
import net.itsmeeandrew.aoe4esports.model.Tournament
import net.itsmeeandrew.aoe4esports.service.TournamentRoundService
import net.itsmeeandrew.aoe4esports.service.TournamentService
import org.springframework.stereotype.Component

@Component
class Jobs(
    private val tournamentService: TournamentService,
    private val tournamentRoundService: TournamentRoundService,
    private val liquipediaClient: LiquipediaClient
) {

    @PostConstruct
    fun init() {
        val tournament = addTournament("N4C/1")
        if (tournament != null) {
            addTournamentRounds(tournament)
        }
    }

    fun addTournament(tournamentId: String): Tournament? {
        val tournament = liquipediaClient.getTournament("N4C/1")
        val isSuccess = tournamentService.createTournament(tournament)
        return if (isSuccess) {
            println("Sucessfully added tournament: ${tournament.name}")
            tournament
        } else null
    }

    fun addTournamentRounds(tournament: Tournament) {
        val tournamentRounds = liquipediaClient.getTournamentRounds(tournament)
        tournamentRounds.forEach { tr ->
            val isSuccess = tournamentRoundService.createTournamentRound(tr)
            if (isSuccess) println("Successfully added tournament round: ${tr.name}")
        }
    }
}