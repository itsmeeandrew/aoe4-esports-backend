package net.itsmeeandrew.aoe4esports.util

enum class TournamentTier(private val tierName: String) {
    S("S"),
    A("A"),
    UNKNOWN("unknown");

    override fun toString(): String {
        return tierName
    }
}

enum class TournamentFormat(private val formatName: String) {
    ONE_VS_ONE("1v1"),
    TEAM("team"),
    FFA("ffa"),
    UNKNOWN("unknown");

    override fun toString(): String {
        return formatName
    }
}