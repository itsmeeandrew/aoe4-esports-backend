package net.itsmeeandrew.aoe4esports.repository

import net.itsmeeandrew.aoe4esports.model.Tournament
import net.itsmeeandrew.aoe4esports.model.TournamentRowMapper
import org.springframework.dao.DuplicateKeyException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementSetter
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
class TournamentRepository(private val jdbc: JdbcTemplate) {
    fun findById(id: String): Tournament? {
        val sql = "SELECT * FROM Tournament WHERE id = ?"
        return try {
            jdbc.query(sql, PreparedStatementSetter { ps ->
                ps.setString(1, id)
            }, TournamentRowMapper()).firstOrNull()
        } catch (e: Exception) {
            println("Error while trying to find tournament by id. ${e.message}")
            null
        }
    }

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

    fun getMaps(id: String): List<String> {
        val sql = """
            SELECT DISTINCT map.name
            FROM Match m
            LEFT JOIN Series s on s.id = m.series_id
            LEFT JOIN TournamentRound tr on tr.id = s.tournament_round_id
            LEFT JOIN Tournament t on t.id = tr.tournament_id
            LEFT JOIN Map map on map.id = m.map_id
            WHERE t.id = ? AND 
            map.name IS NOT NULL
        """.trimIndent()

        return try {
            jdbc.query(sql, PreparedStatementSetter { ps ->
                ps.setString(1, id)
            }, RowMapper { rs, _ -> rs.getString(1) })
        } catch (e: Exception) {
            println("Error while trying to get maps for tournament. ${e.message}")
            listOf()
        }
    }
}