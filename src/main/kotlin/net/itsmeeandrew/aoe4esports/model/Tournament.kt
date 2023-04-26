package net.itsmeeandrew.aoe4esports.model

import net.itsmeeandrew.aoe4esports.util.TournamentFormat
import net.itsmeeandrew.aoe4esports.util.TournamentTier
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
