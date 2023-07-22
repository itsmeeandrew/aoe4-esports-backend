package net.itsmeeandrew.aoe4esports.service

import net.itsmeeandrew.aoe4esports.model.Tournament
import net.itsmeeandrew.aoe4esports.repository.TournamentRepository
import org.springframework.stereotype.Service

@Service
class TournamentService(private val tournamentRepository: TournamentRepository) {
    fun findById(id: String): Tournament? {
        return tournamentRepository.findById(id)
    }

    fun findAll(): List<Tournament> {
        return tournamentRepository.findAll()
    }

    fun findOngoing(): List<Tournament> {
        return tournamentRepository.findOngoing()
    }

    fun create(t: Tournament): Tournament? {
        return tournamentRepository.create(t)
    }

    fun deleteById(id: String): Boolean {
        return tournamentRepository.deleteById(id)
    }

    fun getMaps(id: String): List<String> {
        return tournamentRepository.getMaps(id).sorted()
    }
}