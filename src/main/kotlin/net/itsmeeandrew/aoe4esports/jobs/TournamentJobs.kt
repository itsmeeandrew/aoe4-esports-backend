package net.itsmeeandrew.aoe4esports.jobs

import jakarta.annotation.PostConstruct
import net.itsmeeandrew.aoe4esports.client.LiquipediaClient
import net.itsmeeandrew.aoe4esports.service.TournamentService
import org.springframework.stereotype.Component

@Component
class TournamentJobs(
    private val tournamentService: TournamentService,
    private val liquipediaClient: LiquipediaClient
) {

    @PostConstruct
    fun addTournament() {
        val t = liquipediaClient.getTournament("N4C/1")
        val res = tournamentService.createTournament(t)
        if (res) {
            println("Sucessfully added tournament: ${t.name}")
        } else {
            println("Error while trying to add tournament ${t.name}.")
        }
    }
}