package net.itsmeeandrew.aoe4esports.service

import net.itsmeeandrew.aoe4esports.model.TournamentRoundPhase
import net.itsmeeandrew.aoe4esports.repository.TournamentRoundPhaseRepository
import org.springframework.stereotype.Service

@Service
class TournamentRoundPhaseService(private val tournamentRoundPhaseRepository: TournamentRoundPhaseRepository) {
    fun create(tournamentRoundPhase: TournamentRoundPhase): TournamentRoundPhase? {
        if (tournamentRoundPhase.name.isBlank()) {
            return null
        }
        return tournamentRoundPhaseRepository.create(tournamentRoundPhase)
    }

    fun find(tournamentRoundPhase: TournamentRoundPhase): TournamentRoundPhase? {
        return tournamentRoundPhaseRepository.find(tournamentRoundPhase)
    }

    fun findOrCreate(tournamentRoundPhase: TournamentRoundPhase): TournamentRoundPhase? {
        return find(tournamentRoundPhase) ?: create(tournamentRoundPhase)
    }

    fun updateBestOf(id: Int, bestOf: Int): Boolean {
        return tournamentRoundPhaseRepository.updateBestOf(id, bestOf)
    }
}