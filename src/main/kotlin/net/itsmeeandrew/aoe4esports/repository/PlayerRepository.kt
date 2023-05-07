package net.itsmeeandrew.aoe4esports.repository

import net.itsmeeandrew.aoe4esports.model.Player
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementSetter
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.Statement

@Repository
class PlayerRepository(private val jdbc: JdbcTemplate) {
    fun findById(id: Int): Player? {
        val sql = "SELECT * FROM Player WHERE id = ?"

        return jdbc.query(sql, PreparedStatementSetter { ps ->
            ps.setInt(1, id)
        }, ResultSetExtractor { rs ->
            if (rs.next()) {
                Player(
                    rs.getInt("id"),
                    rs.getString("name")
                )
            } else {
                null
            }
        })
    }

    fun findByName(name: String): Player? {
        val sql = "SELECT * FROM Player WHERE name = ?"

        return jdbc.query(sql, PreparedStatementSetter { ps ->
            ps.setString(1, name)
        }, ResultSetExtractor { rs ->
            if (rs.next()) {
                Player(
                    rs.getInt("id"),
                    rs.getString("name")
                )
            } else {
                null
            }
        })
    }

    fun createPlayer(player: Player): Player? {
        val sql = """
            INSERT INTO Player (name) 
            VALUES (?)
        """.trimIndent()
        val keyHolder = GeneratedKeyHolder()

        return try {
            jdbc.update({ connection ->
                val ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                ps.setString(1, player.name)
                ps
            }, keyHolder)

            Player(keyHolder.key?.toInt(), player.name)
        } catch (e: Exception) {
            println("Error while adding Player to the database. ${e.message}")
            null
        }
    }
}