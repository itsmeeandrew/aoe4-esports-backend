package net.itsmeeandrew.aoe4esports.repository

import net.itsmeeandrew.aoe4esports.model.Tournament
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class TournamentRepository(private val jdbc: JdbcTemplate) {
    fun createTournament(t: Tournament): Tournament? {
        try {
            val queryString = """
                    INSERT INTO Tournament (id, name, start_date, end_date, format, logo_url, twitch_url, tier)
                    VALUES ('${t.id}', '${t.name}', '${t.startDate}', '${t.endDate}', '${t.format}', '${t.logoUrl}', '${t.twitchUrl}', '${t.tier}')
                """.trimIndent()
            jdbc.update(queryString)
            return t
        } catch (e: Exception) {
            println("Error while trying to create tournament. ${e.message}")
            return null
        }
    }
}