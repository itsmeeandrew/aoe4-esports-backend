package net.itsmeeandrew.aoe4esports.repository

import net.itsmeeandrew.aoe4esports.model.Series
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.Statement

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
}