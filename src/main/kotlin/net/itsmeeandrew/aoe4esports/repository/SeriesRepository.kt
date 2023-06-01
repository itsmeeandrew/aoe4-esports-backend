package net.itsmeeandrew.aoe4esports.repository

import net.itsmeeandrew.aoe4esports.model.Series
import net.itsmeeandrew.aoe4esports.model.SeriesRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementSetter
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.Statement

@Repository
class SeriesRepository(private val jdbc: JdbcTemplate) {
    fun create(series: Series): Series? {
        val sql = """
            INSERT INTO Series
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()
        val keyHolder = GeneratedKeyHolder()

        return try {
            jdbc.update({ connection ->
                val ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                ps.setInt(1, series.homePlayerId)
                ps.setInt(2, series.awayPlayerId)
                ps.setInt(3, series.homeScore)
                ps.setInt(4, series.awayScore)
                ps.setObject(5, series.date)
                ps.setObject(6,series.time)
                ps.setString(7, series.tournamentRoundId)
                ps.setInt(8, series.tournamentRoundPhaseId)
                ps
            }, keyHolder)

            val createdSeries = series.copy(id = keyHolder.key?.toInt())
            createdSeries
        } catch (e: Exception) {
            println("Error while adding Series to the database. ${e.message}")
            null
        }
    }

    fun find(series: Series): Series? {
        val sql = """
            SELECT * FROM Series
            WHERE home_player_id = ? AND
            away_player_id = ? AND
            tournament_round_id = ? AND
            tournament_round_phase_id = ?
        """.trimIndent()

        return jdbc.query(sql, PreparedStatementSetter { ps ->
                ps.setInt(1, series.homePlayerId)
                ps.setInt(2, series.awayPlayerId)
                ps.setString(3, series.tournamentRoundId)
                ps.setInt(4, series.tournamentRoundPhaseId)
            }, SeriesRowMapper()).firstOrNull()
    }

    fun updateScores(id: Int, homeScore: Int, awayScore: Int): Boolean {
        val sql = """
            UPDATE Series
            SET home_score = ?,
            away_score = ?
            WHERE id = ?
        """.trimIndent()

        return try {
            jdbc.update(sql, homeScore, awayScore, id)
            true
        } catch (e: Exception) {
            println("Error while executing updateScores in Series. ${e.message}")
            false
        }
    }
}