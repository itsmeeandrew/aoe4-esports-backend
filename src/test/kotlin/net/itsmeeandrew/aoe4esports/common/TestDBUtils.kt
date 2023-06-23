package net.itsmeeandrew.aoe4esports.common

import net.itsmeeandrew.aoe4esports.model.Series
import net.itsmeeandrew.aoe4esports.model.Tournament
import java.time.LocalDate
import java.time.LocalTime

class TestDBUtils {
    companion object {
        const val TOURNAMENT_ID = "N4C/1"
        const val TOURNAMENT_ROUND_ID = "N4C/1"
        const val TOURNAMENT_ROUND_PHASE_ID = 1
        const val MAP_DRY_ARABIA_ID = 1
        const val MAP_LIPANY_ID = 2
        const val PLAYER_BEASTY_ID = 1
        const val PLAYER_MARINELORD_ID = 2
        const val PLAYER_DEMU_ID = 3
        const val PLAYER_1PUPPYPAW_ID = 4
        const val PLAYER_LEENOCK_ID = 5
        val ONGOING_TOURNAMENT = Tournament(
            LocalDate.now().plusWeeks(1),
            TournamentFormat.ONE_VS_ONE,
            "Testtournament",
            "testlogourl",
            "Teszt tournament",
            LocalDate.now().minusWeeks(1),
            TournamentTier.S,
            "testtwitchurl"
        )
        const val SERIES_ID = 1
        val SERIES = Series(
            PLAYER_MARINELORD_ID,
            2,
            LocalDate.parse("2022-03-08"),
            SERIES_ID,
            PLAYER_BEASTY_ID,
            4,
            LocalTime.parse("15:00:00"),
            TOURNAMENT_ROUND_ID,
            TOURNAMENT_ROUND_PHASE_ID
        )

    }
}