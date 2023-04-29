package net.itsmeeandrew.aoe4esports.repository

import net.itsmeeandrew.aoe4esports.model.Tournament
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class TournamentRepository(private val jdbc: JdbcTemplate) {
    fun createTournament(tournament: Tournament): Tournament? {
        return try {
            val queryString = """
                        INSERT INTO Tournament (id, name, start_date, end_date, format, logo_url, twitch_url, tier)
                        VALUES ('${tournament.id}', '${tournament.name}', '${tournament.startDate}', '${tournament.endDate}', '${tournament.format}', '${tournament.logoUrl}', '${tournament.twitchUrl}', '${tournament.tier}')
                    """.trimIndent()
            jdbc.update(queryString)
            tournament
        } catch (e: Exception) {
            println("Error while trying to create tournament. ${e.message}")
            null
        }
    }
}