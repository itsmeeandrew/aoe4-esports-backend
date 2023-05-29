package net.itsmeeandrew.aoe4esports.common.`interface`

import net.itsmeeandrew.aoe4esports.model.Match
import net.itsmeeandrew.aoe4esports.model.Series
import net.itsmeeandrew.aoe4esports.model.Tournament
import net.itsmeeandrew.aoe4esports.model.TournamentRound

interface IParseHandler {
    fun getTournamentIds(): List<String>
    fun handleTournament(tournament: Tournament)
    fun handleTournamentRound(tournamentRound: TournamentRound)
    fun handleSeriesAndMatches(series: Series, matches: List<Match>)
}