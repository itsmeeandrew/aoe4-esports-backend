package net.itsmeeandrew.aoe4esports.service

import net.itsmeeandrew.aoe4esports.model.Match
import net.itsmeeandrew.aoe4esports.repository.MatchRepository
import org.springframework.stereotype.Service

@Service
class MatchService(private val matchRepository: MatchRepository) {
    fun create(match: Match): Match? = matchRepository.create(match)


    fun findBySeriesId(id: Int): List<Match> {
        return matchRepository.findBySeriesId(id)
    }

    fun deleteMany(matches: List<Match>): Boolean {
        if (matches.isEmpty()) {
            return true
        }
        return matchRepository.deleteMany(matches)
    }
}