package net.itsmeeandrew.aoe4esports.repository

import net.itsmeeandrew.aoe4esports.model.Match
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.Statement
import java.sql.Types

@Repository
class MatchRepository(private val jdbc: JdbcTemplate) {
    fun create(match: Match): Match? {
        val sql = """
            INSERT INTO Match (map_id, winner_player_id, series_id, home_civilization_id, away_civilization_id) 
            VALUES (?, ?, ?, ?, ?)
        """.trimIndent()
        val keyHolder = GeneratedKeyHolder()

        return try {
            jdbc.update({ connection ->
                val ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                ps.setObject(1, match.mapId, Types.INTEGER)
                ps.setObject(2, match.winnerPlayerId, Types.INTEGER)
                ps.setObject(3, match.seriesId, Types.INTEGER)
                ps.setInt(4, match.homeCivilizationId)
                ps.setInt(5, match.awayCivilizationId)
                ps
            }, keyHolder)

            match.copy(id = keyHolder.key?.toInt())
        } catch (e: Exception) {
            println("Error while adding Match to the database. ${e.message}")
            null
        }
    }
}