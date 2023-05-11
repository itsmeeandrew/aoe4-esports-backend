package net.itsmeeandrew.aoe4esports.repository

import net.itsmeeandrew.aoe4esports.model.Series
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementSetter
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.Statement
import java.time.LocalDate
import java.time.LocalTime

@Repository
class SeriesRepository(private val jdbc: JdbcTemplate) {
    fun addSeries(series: Series): Series? {
        val sql = """
            INSERT INTO Series (home_player_id, home_score, away_player_id, away_score, date, time, best_of, bracket_round, tournament_round_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()
        val keyHolder = GeneratedKeyHolder()

        return try {
            jdbc.update({ connection ->
                val ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                ps.setInt(1, series.homePlayerId)
                ps.setInt(2, series.homeScore)
                ps.setInt(3, series.awayPlayerId)
                ps.setInt(4, series.awayScore)
                ps.setObject(5, series.date)
                ps.setObject(6, series.time)
                ps.setInt(7, series.bestOf)
                ps.setString(8, series.bracketRound)
                ps.setString(9, series.tournamentRoundId)
                ps
            }, keyHolder)

            val createdSeries = series.copy(id = keyHolder.key?.toInt())
            createdSeries
        } catch (e: Exception) {
            println("Error while adding Series to the database. ${e.message}")
            null
        }
    }

    fun findByDetails(homePlayerId: Int, awayPlayerId: Int, tournamentRoundId: String, bracketRound: String): Series? {
        val sql = """
            SELECT * FROM Series
            WHERE home_player_id = ? AND
            away_player_id = ? AND
            tournament_round_id = ? AND
            bracket_round = ?
        """.trimIndent()

        return jdbc.query(sql, PreparedStatementSetter { ps ->
                ps.setInt(1, homePlayerId)
                ps.setInt(2, awayPlayerId)
                ps.setString(3, tournamentRoundId)
                ps.setString(4, bracketRound)
            }, ResultSetExtractor { rs ->
                if (rs.next()) {
                    Series(
                        awayPlayerId = rs.getInt("away_player_id"),
                        awayScore = rs.getInt("away_score"),
                        bestOf = rs.getInt("best_of"),
                        bracketRound = rs.getString("bracket_round"),
                        date = rs.getObject("date", LocalDate::class.java),
                        id = rs.getInt("id"),
                        homePlayerId = rs.getInt("home_player_id"),
                        homeScore = rs.getInt("home_score"),
                        time = rs.getObject("time", LocalTime::class.java),
                        tournamentRoundId = rs.getString("tournament_round_id")

                    )
                } else null
        })

    }
}