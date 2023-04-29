package net.itsmeeandrew.aoe4esports.repository

import net.itsmeeandrew.aoe4esports.model.TournamentRound
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class TournamentRoundRepository(private val jdbc: JdbcTemplate) {
    fun createTournamentRound(tournamentRound: TournamentRound): TournamentRound? {
        return try {
            val queryString = """
                        INSERT INTO TournamentRound (id, name, tournament_id)
                        VALUES ('${tournamentRound.id}', '${tournamentRound.name}', '${tournamentRound.tournamentId}')
                    """.trimIndent()
            jdbc.update(queryString)
            tournamentRound
        } catch (e: Exception) {
            println("Error while trying to create tournament round ${tournamentRound}. ${e.message}")
            null
        }
    }
}