package net.itsmeeandrew.aoe4esports.model

import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.time.LocalDate
import java.time.LocalTime

data class PopulatedSeries(
    val id: Int,
    val awayPlayer: String,
    val homePlayer: String,
    val homeScore: Int,
    val awayScore: Int,
    val date: LocalDate,
    val time: LocalTime,
    val tournament: String,
    val tournamentRound: String,
    val tournamentRoundPhase: String,
    val logoUrl: String
)

class PopulatedSeriesRowMapper: RowMapper<PopulatedSeries> {
    override fun mapRow(rs: ResultSet, rowNum: Int): PopulatedSeries {
        return PopulatedSeries(
            id = rs.getInt("id"),
            awayPlayer = rs.getString("away_player"),
            homePlayer = rs.getString("home_player"),
            homeScore = rs.getInt("home_score"),
            awayScore = rs.getInt("away_score"),
            date = rs.getObject("date", LocalDate::class.java),
            time = rs.getObject("time", LocalTime::class.java),
            tournament = rs.getString("tournament_name"),
            tournamentRound = rs.getString("tournament_round_name"),
            tournamentRoundPhase = rs.getString("tournament_round_phase_name"),
            logoUrl = rs.getString("logo_url")
        )
    }
}
