package net.itsmeeandrew.aoe4esports.repository

import net.itsmeeandrew.aoe4esports.common.TournamentFormat
import net.itsmeeandrew.aoe4esports.common.TournamentTier
import net.itsmeeandrew.aoe4esports.model.Tournament
import org.springframework.dao.DuplicateKeyException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class TournamentRepository(private val jdbc: JdbcTemplate) {
    fun findAll(): List<Tournament> {
        val sql = "SELECT * FROM Tournament"
        return jdbc.query(sql) { rs, _ ->
            Tournament(
                rs.getDate("end_date").toLocalDate(),
                TournamentFormat.from(rs.getString("format")),
                rs.getString("id"),
                rs.getString("logo_url"),
                rs.getString("name"),
                rs.getDate("start_date").toLocalDate(),
                TournamentTier.valueOf(rs.getString("tier")),
                rs.getString("twitch_url")
            )
        }
    }

    fun create(tournament: Tournament): Tournament? {
        return try {
            val sql = """
                        INSERT INTO Tournament (id, name, start_date, end_date, format, logo_url, twitch_url, tier)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """.trimIndent()
            jdbc.update(sql) { ps ->
                ps.setString(1, tournament.id)
                ps.setString(2, tournament.name)
                ps.setObject(3, tournament.startDate)
                ps.setObject(4, tournament.endDate)
                ps.setString(5, tournament.format.toString())
                ps.setString(6, tournament.logoUrl)
                ps.setString(7, tournament.twitchUrl)
                ps.setString(8, tournament.tier.toString())
            }
            tournament
        } catch (e: DuplicateKeyException) {
            println("[TOURNAMENT REPOSITORY]: ${tournament.name} already exists.")
            null
        } catch (e: Exception) {
            println("[TOURNAMENT REPOSITORY]: Error while trying to create tournament. ${e.message}")
            println("Exception name: ${e.javaClass.canonicalName}")
            null
        }
    }
}