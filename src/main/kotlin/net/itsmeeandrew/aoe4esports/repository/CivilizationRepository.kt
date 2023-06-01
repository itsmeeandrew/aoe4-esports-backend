package net.itsmeeandrew.aoe4esports.repository

import net.itsmeeandrew.aoe4esports.model.Civilization
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementSetter
import org.springframework.stereotype.Repository

@Repository
class CivilizationRepository(private val jdbc: JdbcTemplate) {

    fun findAll(): List<Civilization> {
        val sql = "SELECT * FROM Civilization"
        return jdbc.query(sql, DataClassRowMapper(Civilization::class.java))
    }

    fun findByName(name: String): Civilization? {
        val sql = """
            SELECT * FROM Civilization
            WHERE name = ?
        """.trimIndent()

        return jdbc.query(sql, PreparedStatementSetter { ps ->
            ps.setString(1, name)
        }, DataClassRowMapper(Civilization::class.java)).firstOrNull()
    }
}