package net.itsmeeandrew.aoe4esports.repository

import net.itsmeeandrew.aoe4esports.model.GMap
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementSetter
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.Statement

@Repository
class MapRepository(private val jdbc: JdbcTemplate) {
    fun findByName(name: String): GMap? {
        val sql = """
            SELECT * FROM Map
            WHERE name = ?
        """.trimIndent()

        return jdbc.query(sql, PreparedStatementSetter { ps ->
            ps.setString(1, name)
        }, DataClassRowMapper(GMap::class.java)).firstOrNull()
    }

    fun create(map: GMap): GMap? {
        val sql = """
            INSERT INTO Map
            VALUES (?)
        """.trimIndent()
        val keyHolder = GeneratedKeyHolder()

        return try {
            jdbc.update({ connection ->
                val ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                ps.setString(1, map.name)
                ps
            }, keyHolder)

            map.copy(id = keyHolder.key?.toInt())
        } catch (e: Exception) {
            println("Error while trying to create Map: ${e.message}")
            null
        }
    }
}