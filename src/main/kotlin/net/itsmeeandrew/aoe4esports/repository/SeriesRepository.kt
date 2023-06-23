package net.itsmeeandrew.aoe4esports.repository

import net.itsmeeandrew.aoe4esports.model.PopulatedSeries
import net.itsmeeandrew.aoe4esports.model.PopulatedSeriesRowMapper
import net.itsmeeandrew.aoe4esports.model.Series
import net.itsmeeandrew.aoe4esports.model.SeriesRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementSetter
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.Statement
import java.time.LocalDate
import java.time.LocalTime

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

    fun findLatest(n: Int): List<PopulatedSeries> {
        val sql = """
            SELECT TOP $n
            s.id,
            p1.name as "home_player",
            p2.name as "away_player",
            s.home_score,
            s.away_score,
            s.date,
            s.time,
            t.name as "tournament_name",
            t.logo_url,
            tr.name as "tournament_round_name",
            trp.name as "tournament_round_phase_name"
            FROM Series s
            LEFT JOIN Player p1 ON p1.id=s.home_player_id
            LEFT JOIN Player p2 ON p2.id=s.away_player_id
            LEFT JOIN TournamentRoundPhase trp ON s.tournament_round_phase_id=trp.id
            LEFT JOIN TournamentRound tr ON s.tournament_round_id=tr.id
            LEFT JOIN Tournament t ON tr.tournament_id=t.id
            ORDER BY [date] DESC
        """.trimIndent()

        return try {
            jdbc.query(sql, PopulatedSeriesRowMapper())
        } catch (e: Exception) {
            println("Error while executing findLatestFive. ${e.message}")
            listOf()
        }
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

    fun updateTime(id: Int, time: LocalTime): Boolean {
        val sql = """
            UPDATE Series
            SET time = ?
            WHERE id = ?
        """.trimIndent()

        return try {
            jdbc.update(sql, time, id)
            true
        } catch (e: Exception) {
            println("Error while executing updateTime in Series. ${e.message}")
            false
        }
    }

    fun updateDate(id: Int, date: LocalDate): Boolean {
        val sql = """
            UPDATE Series
            SET date = ?
            WHERE id = ?
        """.trimIndent()

        return try {
            jdbc.update(sql, date, id)
            true
        } catch (e: Exception) {
            println("Error while executing updateDate in Series. ${e.message}")
            false
        }
    }

    fun delete(series: Series): Boolean {
        val sql = """
            DELETE FROM Series
            WHERE home_player_id = ? AND
            away_player_id = ? AND
            tournament_round_id = ? AND
            tournament_round_phase_id = ?
        """.trimIndent()

        return try {
            jdbc.update(sql, series.homePlayerId, series.awayPlayerId, series.tournamentRoundId, series.tournamentRoundPhaseId)
            true
        } catch (e: Exception) {
            println("Error while trying to delete Series: ${e.message}")
            false
        }
    }
}