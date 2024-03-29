package net.itsmeeandrew.aoe4esports.controller

import net.itsmeeandrew.aoe4esports.common.decodeTournamentId
import net.itsmeeandrew.aoe4esports.common.encodeTournamentId
import net.itsmeeandrew.aoe4esports.model.Tournament
import net.itsmeeandrew.aoe4esports.service.TournamentService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["http://localhost:3000"])
class TournamentController(private val tournamentService: TournamentService) {

    @GetMapping("/tournaments/{id}")
    fun getTournamentById(@PathVariable id: String): Tournament {
        val tournament = tournamentService.findById(decodeTournamentId(id))
        if (tournament == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Tournament does not exist with ID $id.")
        } else return tournament
    }

    @GetMapping("/tournaments/{id}/maps")
    fun getTournamentMaps(@PathVariable id: String): List<String> {
        return tournamentService.getMaps(decodeTournamentId(id))
    }

    @GetMapping("/tournaments/ongoing")
    fun getOngoingTournaments(): List<Tournament> {
        return tournamentService.findOngoing().map { t -> t.copy(id = encodeTournamentId(t.id)) }
    }
}