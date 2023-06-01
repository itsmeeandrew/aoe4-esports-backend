package net.itsmeeandrew.aoe4esports.model

import net.itsmeeandrew.aoe4esports.common.TournamentFormat
import net.itsmeeandrew.aoe4esports.common.TournamentTier
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.time.LocalDate

data class Tournament(
    val endDate: LocalDate,
    val format: TournamentFormat,
    val id: String,
    val logoUrl: String,
    val name: String,
    val startDate: LocalDate,
    val tier: TournamentTier,
    val twitchUrl: String
) {
    override fun toString(): String {
        return "Tournament: $name\n" +
                "Id: $id\n" +
                "Start date: $startDate\n" +
                "End date: $endDate\n" +
                "Format: $format\n" +
                "Tier: $tier\n" +
                "Logo URL: $logoUrl\n" +
                "Twitch URL: $twitchUrl"
    }
}

class TournamentRowMapper: RowMapper<Tournament> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Tournament {
        return Tournament(
            rs.getDate("end_date").toLocalDate(),
            TournamentFormat.from(rs.getString("format")),
            rs.getString("id"),
            rs.getString("logo_url"),
            rs.getString("name"),
            rs.getDate("start_date").toLocalDate(),
            TournamentTier.valueOf(rs.getString("tier")),
            rs.getString("twitch_url")
        )
    }
}