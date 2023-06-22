package net.itsmeeandrew.aoe4esports.repository

import net.itsmeeandrew.aoe4esports.model.Tournament
import net.itsmeeandrew.aoe4esports.model.TournamentRowMapper
import org.springframework.dao.DuplicateKeyException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class TournamentRepository(private val jdbc: JdbcTemplate) {
    fun findAll(): List<Tournament> {
        val sql = "SELECT * FROM Tournament"
        return jdbc.query(sql, TournamentRowMapper())
    }

    fun findOngoing(): List<Tournament> {
        val sql = """
            SELECT * FROM Tournament
            WHERE start_date <= CAST( GETDATE() AS Date ) AND
            end_date >= CAST( GETDATE() AS Date)
        """.trimIndent()
        return jdbc.query(sql, TournamentRowMapper())
    }

    fun create(tournament: Tournament): Tournament? {
        return try {
            val sql = """
                INSERT INTO Tournament
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

    fun deleteById(id: String): Boolean {
        val sql = """
            DELETE FROM Tournament
            WHERE id = ?
        """.trimIndent()

        return try {
            jdbc.update(sql, id)
            true
        } catch (e: Exception) {
            println("Error while trying to delete tournament. ${e.message}")
            false
        }
    }
}