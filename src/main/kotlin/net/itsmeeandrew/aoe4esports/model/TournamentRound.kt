package net.itsmeeandrew.aoe4esports.model

data class TournamentRound(
    val id: String,
    val name: String,
    val tournamentId: String
) {
    override fun toString(): String {
        return """
            Tournament round: $name
            Id: $id,
            Tournament id: $tournamentId 
        """.trimIndent()
    }
}
