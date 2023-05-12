package net.itsmeeandrew.aoe4esports.service

import net.itsmeeandrew.aoe4esports.model.TournamentRound
import net.itsmeeandrew.aoe4esports.repository.TournamentRoundRepository
import org.springframework.stereotype.Service

@Service
class TournamentRoundService(private val tournamentRoundRepository: TournamentRoundRepository) {
    fun create(tournamentRound: TournamentRound): TournamentRound? {
        return tournamentRoundRepository.create(tournamentRound)
    }
}