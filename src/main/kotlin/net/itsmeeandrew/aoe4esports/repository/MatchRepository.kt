package net.itsmeeandrew.aoe4esports.repository

import net.itsmeeandrew.aoe4esports.model.Match
import net.itsmeeandrew.aoe4esports.model.PopulatedMatch
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementSetter
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.Statement
import java.sql.Types

@Repository
class MatchRepository(private val jdbc: JdbcTemplate) {
    fun findBySeriesId(id: Int): List<Match> {
        val sql = "SELECT * FROM Match WHERE series_id = ?"
        return jdbc.query(sql, PreparedStatementSetter { ps ->
            ps.setInt(1, id)
        }, DataClassRowMapper(Match::class.java))
    }

    fun findPopulatedBySeriesId(id: Int): List<PopulatedMatch> {
        val sql = """
            SELECT 
                mtch.id,
                mtch.series_id,
                m.name AS "map",
                c1.name AS "home_civilization",
                c2.name AS "away_civilization",
                p.name AS "winner_player"
            FROM [Match] mtch
            LEFT JOIN Map m ON mtch.map_id=m.id
            LEFT JOIN Civilization c1 ON mtch.home_civilization_id=c1.id
            LEFT JOIN Civilization c2 ON mtch.away_civilization_id=c2.id
            LEFT JOIN Player p ON mtch.winner_player_id=p.id
            WHERE series_id = ?
        """.trimIndent()

        return try {
            jdbc.query(sql, PreparedStatementSetter { ps ->
                ps.setInt(1, id)
            }, DataClassRowMapper(PopulatedMatch::class.java))
        } catch(e: Exception) {
            println("Error while executing findPopulatedMatchesById. ${e.message}")
            listOf()
        }
    }

    fun create(match: Match): Match? {
        val sql = """
            INSERT INTO Match
            VALUES (?, ?, ?, ?, ?)
        """.trimIndent()
        val keyHolder = GeneratedKeyHolder()

        return try {
            jdbc.update({ connection ->
                val ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                ps.setObject(1, match.mapId, Types.INTEGER)
                ps.setObject(2, match.awayCivilizationId)
                ps.setObject(3, match.homeCivilizationId)
                ps.setObject(4, match.seriesId, Types.INTEGER)
                ps.setObject(5, match.winnerPlayerId, Types.INTEGER)
                ps
            }, keyHolder)

            match.copy(id = keyHolder.key?.toInt())
        } catch (e: Exception) {
            println("Error while adding Match to the database. ${e.message}")
            null
        }
    }

    fun deleteMany(matches: List<Match>): Boolean {
        val sql = """
            DELETE FROM Match
            WHERE id IN (:ids)
        """.trimIndent()

        val namedParameterJdbc = NamedParameterJdbcTemplate(jdbc)
        val parameters = MapSqlParameterSource()
            .addValue("ids", matches.map { m -> m.id })

        return try {
            namedParameterJdbc.update(sql, parameters)
            true
        } catch (e: Exception) {
            println("Error while executing deleteMany in MatchRepository. ${e.message}")
            false
        }
    }
}