package net.itsmeeandrew.aoe4esports.client

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import net.itsmeeandrew.aoe4esports.common.TournamentTier
import net.itsmeeandrew.aoe4esports.model.TournamentRound
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

    private fun toJson(str: String?): JsonNode {
        println("Pausing for 2s before another call.")
        Thread.sleep(2000)
        return ObjectMapper().readTree(str ?: " ")
    }

    fun getTournamentIds(tier: TournamentTier): List<String> {
        val response = client.get()
            .uri(getTournamentsPageUri(tier))
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
        val resJson = toJson(response)
        val htmlString = resJson.get("parse").get("text").get("*").asText()

        return liquipediaParser.getTournamentIds(htmlString);
    }

    fun getTournamentParser(tournamentId: String): LiquipediaParser.TournamentParser {
        val response = client.get()
            .uri(getPageUri(tournamentId))
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        val resJson = toJson(response)
        val metaData = resJson.at("/parse/properties")
        val logoUrl = metaData.firstOrNull { it["name"].textValue() == "metaimageurl" }?.get("*")?.asText() ?: ""
        val name = metaData.firstOrNull { it["name"].textValue() == "displaytitle" }?.get("*")?.asText() ?: ""

        val htmlString = resJson.at("/parse/text/*").asText()

        return liquipediaParser.TournamentParser(htmlString, tournamentId, logoUrl, name)
    }

    fun getTournamentRoundParser(tournamentRound: TournamentRound): LiquipediaParser.TournamentRoundParser {
        val response = client.get()
            .uri(getPageUri(tournamentRound.id))
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        val resJson = toJson(response)
        val htmlString = resJson.at("/parse/text/*").asText()

        return liquipediaParser.TournamentRoundParser(htmlString, tournamentRound)
    }
}