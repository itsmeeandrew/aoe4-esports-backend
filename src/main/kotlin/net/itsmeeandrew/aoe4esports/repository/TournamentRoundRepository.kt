package net.itsmeeandrew.aoe4esports.repository

import net.itsmeeandrew.aoe4esports.model.TournamentRound
import org.springframework.dao.DuplicateKeyException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class TournamentRoundRepository(private val jdbc: JdbcTemplate) {
    fun create(tournamentRound: TournamentRound): TournamentRound? {
        return try {
            val sql = """
                        INSERT INTO TournamentRound
                        VALUES (?, ?, ?)
                    """.trimIndent()
            jdbc.update(sql) { ps ->
                ps.setString(1, tournamentRound.id)
                ps.setString(2, tournamentRound.name)
                ps.setString(3, tournamentRound.tournamentId)
            }
            tournamentRound
        } catch (e: DuplicateKeyException) {
            println("[TOURNAMENTROUND REPOSITORY]: ${tournamentRound.name} already exists.")
            null
        } catch (e: Exception) {
            println("[TOURNAMENTROUND REPOSITORY]: Error while trying to create tournament round. ${e.message}")
            null
        }
    }
}