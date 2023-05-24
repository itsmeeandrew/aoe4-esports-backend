package net.itsmeeandrew.aoe4esports.repository

import net.itsmeeandrew.aoe4esports.model.TournamentRoundPhase
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementSetter
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.Statement

@Repository
class TournamentRoundPhaseRepository(private val jdbc: JdbcTemplate) {
    fun create(tournamentRoundPhase: TournamentRoundPhase): TournamentRoundPhase? {
        val sql = """
            INSERT INTO TournamentRoundPhase
            VALUES (?, ?, ?)
        """.trimIndent()
        val keyHolder = GeneratedKeyHolder()

        return try {
            jdbc.update({ connection ->
                val ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                ps.setString(1, tournamentRoundPhase.name)
                ps.setObject(2, tournamentRoundPhase.bestOf)
                ps.setString(3, tournamentRoundPhase.tournamentRoundId)
                ps
            }, keyHolder)

            val createdTournamentRoundPhase = tournamentRoundPhase.copy(id = keyHolder.key?.toInt())
            createdTournamentRoundPhase
        } catch (e: Exception) {
            println("Error while adding TournamentRoundPhase to the database. ${e.message}")
            null
        }
    }

    fun find(tournamentRoundPhase: TournamentRoundPhase): TournamentRoundPhase? {
        val sql = """
            SELECT * FROM TournamentRoundPhase
            WHERE name = ? AND
            tournament_round_id = ?
        """.trimIndent()

        return try {
            jdbc.query(sql, PreparedStatementSetter { ps ->
                ps.setString(1, tournamentRoundPhase.name)
                ps.setObject(2, tournamentRoundPhase.tournamentRoundId)
            }, ResultSetExtractor { rs: ResultSet ->
                if (rs.next()) {
                    TournamentRoundPhase(
                        id = rs.getInt("id"),
                        name = rs.getString("name"),
                        bestOf = rs.getInt("best_of"),
                        tournamentRoundId = rs.getString("tournament_round_id")
                    )
                } else null
            })
        } catch (e: Exception) {
            println("Error while executing findOne in TournamentRoundPhaseRepository. ${e.message}")
            null
        }
    }

    fun updateBestOf(id: Int, bestOf: Int): Boolean {
        val sql = """
            UPDATE TournamentRoundPhase
            SET best_of = ?
            WHERE id = ?
        """.trimIndent()

        return try {
            val updatedRowCount = jdbc.update(sql, bestOf, id)
            if (updatedRowCount > 1) {
                println("Updated more than 1 row in updateBestOf.")
            }
            return updatedRowCount == 1
        } catch (e: Exception) {
            println("Error while executing update in TournamentRoundPhaseRepository. ${e.message}")
            false
        }
    }
}