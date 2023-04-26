package net.itsmeeandrew.aoe4esports.client

import net.itsmeeandrew.aoe4esports.model.Tournament
import net.itsmeeandrew.aoe4esports.util.TournamentFormat
import net.itsmeeandrew.aoe4esports.util.TournamentTier
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class LiquipediaParser {

    private fun cleanAndParseHtmlString(htmlString: String): Document {
        val cleanedHtmlString = htmlString.replace("\\", "")

        return Jsoup.parse(cleanedHtmlString)
    }

    fun getTournamentIds(htmlString: String): List<String> {
        val root = cleanAndParseHtmlString(htmlString)
        val selector = ".divRow .divCell.Tournament.Header b a"
        val tournamentLinkElements = root.select(selector)

        val tournamentIds = tournamentLinkElements.map { e ->
            e.attr("title").replace(" ", "_")
        }

        return tournamentIds
    }

    fun getTournament(htmlString: String, tournamentId: String, name: String, logoUrl: String): Tournament {
        val root = cleanAndParseHtmlString(htmlString)
        val tournamentInfoElement = root.select(".fo-nttax-infobox").first()

        val tournamentInfoMap = tournamentInfoElement
            ?.select("> div")
            ?.filter { it -> it.childNodes().size == 2 }
            ?.mapNotNull {
                val infoKey = it.firstElementChild()?.text()
                val infoValue = it.lastElementChild()?.text()

                if (!infoKey.isNullOrBlank() && !infoValue.isNullOrBlank()) {
                    infoKey.trim().dropLast(1) to infoValue.trim()
                } else {
                    null
                }
            }?.toMap() ?: emptyMap()

        val twitchUrl = tournamentInfoElement?.select("a i.lp-twitch")?.first()?.parent()?.attr("href") ?: ""
        val startDate = LocalDate.parse(tournamentInfoMap["Start Date"])
        val endDate = LocalDate.parse(tournamentInfoMap["End Date"])

        val tierText = tournamentInfoMap["Liquipedia Tier"]
        val tier = when (tierText?.get(0)) {
            'S' -> TournamentTier.S
            'A' -> TournamentTier.A
            else -> TournamentTier.UNKNOWN
        }

        val formatText = tournamentInfoMap["Format"] ?: ""
        val format: TournamentFormat = when {
            formatText.contains("1v1") -> TournamentFormat.ONE_VS_ONE
            formatText.contains("/2v2|3v3|4v4|team/gm") -> TournamentFormat.TEAM
            formatText.contains("FFA") -> TournamentFormat.FFA
            else -> TournamentFormat.UNKNOWN
        }

        return Tournament(endDate, format, tournamentId, logoUrl, name, startDate, tier, twitchUrl)
    }
}