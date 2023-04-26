package net.itsmeeandrew.aoe4esports.repository

import net.itsmeeandrew.aoe4esports.model.Civilization
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class CivilizationRepository(private val jdbc: JdbcTemplate) {

    fun findAll(): List<Civilization> = jdbc.query("SELECT * FROM Civilization") { rs, _ ->
        Civilization(
            rs.getInt("id"),
            rs.getString("name")
        )
    }
}