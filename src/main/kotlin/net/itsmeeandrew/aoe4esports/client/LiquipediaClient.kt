package net.itsmeeandrew.aoe4esports.client

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import net.itsmeeandrew.aoe4esports.model.Tournament
import net.itsmeeandrew.aoe4esports.util.TournamentTier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@Component
class LiquipediaClient(
    private val client: WebClient,
    private val liquipediaParser: LiquipediaParser
) {
    companion object {
        const val apiBase: String = "https://liquipedia.net/ageofempires/api.php"
    }

    private fun getPageUri(pageId: String): URI {
        return UriComponentsBuilder
            .fromUriString(apiBase)
            .queryParam("action", "parse")
            .queryParam("format", "json")
            .queryParam("page", pageId)
            .build()
            .toUri()
    }

    private fun getTournamentsPageUri(tier: TournamentTier): URI {
        return getPageUri("Age_of_Empires_IV/$tier-Tier_Tournaments")
    }

    private fun getJson(str: String?): JsonNode {
        return ObjectMapper().readTree(str ?: " ")
    }

    fun getTournamentIds(tier: TournamentTier): List<String> {
        val response = client.get()
            .uri(getTournamentsPageUri(tier))
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
        val resJson = getJson(response)
        val htmlString = resJson.get("parse").get("text").get("*").asText()

        return liquipediaParser.getTournamentIds(htmlString);
    }

    fun getTournament(tournamentId: String): Tournament {
        val response = client.get()
            .uri(getPageUri(tournamentId))
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        val resJson = getJson(response)

        val metaData = resJson.at("/parse/properties")
        val logoUrl = metaData.firstOrNull { it["name"].textValue() == "metaimageurl" }?.get("*")?.textValue() ?: ""
        val name = metaData.firstOrNull { it["name"].textValue() == "displaytitle" }?.get("*")?.textValue() ?: ""

        val htmlString = resJson.at("/parse/text/*").asText()

        return liquipediaParser.getTournament(htmlString, tournamentId, name, logoUrl)
    }
}