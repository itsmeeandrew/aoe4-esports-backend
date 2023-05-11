package net.itsmeeandrew.aoe4esports.repository

import net.itsmeeandrew.aoe4esports.model.Civilization
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementSetter
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Repository

@Repository
class CivilizationRepository(private val jdbc: JdbcTemplate) {

    fun findAll(): List<Civilization> = jdbc.query("SELECT * FROM Civilization") { rs, _ ->
        Civilization(
            rs.getInt("id"),
            rs.getString("name")
        )
    }

    fun findByName(name: String): Civilization? {
        val sql = """
            SELECT * FROM Civilization
            WHERE name = ?
        """.trimIndent()

        return jdbc.query(sql, PreparedStatementSetter { ps ->
            ps.setString(1, name)
        }, ResultSetExtractor { rs ->
            if (rs.next()) {
                Civilization(
                    rs.getInt("id"),
                    rs.getString("name")
                )
            } else null
        })
    }
}