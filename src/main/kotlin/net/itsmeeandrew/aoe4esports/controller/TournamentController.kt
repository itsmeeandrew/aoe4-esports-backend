package net.itsmeeandrew.aoe4esports.controller

import net.itsmeeandrew.aoe4esports.model.Tournament
import net.itsmeeandrew.aoe4esports.service.TournamentService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class TournamentController(private val tournamentService: TournamentService) {

    @GetMapping("/tournaments/ongoing")
    fun getOngoingTournaments(): List<Tournament> {
        return tournamentService.findOngoing()
    }
}