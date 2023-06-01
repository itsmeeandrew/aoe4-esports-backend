package net.itsmeeandrew.aoe4esports.model

import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.time.LocalDate
import java.time.LocalTime

data class Series(
    val awayPlayerId: Int,
    val awayScore: Int,
    val date: LocalDate?,
    val id: Int?,
    val homePlayerId: Int,
    val homeScore: Int,
    val time: LocalTime?,
    val tournamentRoundId: String,
    val tournamentRoundPhaseId: Int
)

class SeriesRowMapper: RowMapper<Series> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Series {
        return Series(
            awayPlayerId = rs.getInt("away_player_id"),
            awayScore = rs.getInt("away_score"),
            date = rs.getObject("date", LocalDate::class.java),
            id = rs.getInt("id"),
            homePlayerId = rs.getInt("home_player_id"),
            homeScore = rs.getInt("home_score"),
            time = rs.getObject("time", LocalTime::class.java),
            tournamentRoundId = rs.getString("tournament_round_id"),
            tournamentRoundPhaseId = rs.getInt("tournament_round_phase_id")
        )
    }
}