package net.itsmeeandrew.aoe4esports.service

import net.itsmeeandrew.aoe4esports.model.Tournament
import net.itsmeeandrew.aoe4esports.repository.TournamentRepository
import org.springframework.stereotype.Service

@Service
class TournamentService(private val tournamentRepository: TournamentRepository) {
    fun createTournament(t: Tournament): Boolean {
        return tournamentRepository.createTournament(t) != null
    }
}