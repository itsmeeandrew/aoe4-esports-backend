package net.itsmeeandrew.aoe4esports.controller

import net.itsmeeandrew.aoe4esports.model.PopulatedMatch
import net.itsmeeandrew.aoe4esports.service.MatchService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api")
class MatchController(private val matchService: MatchService) {

    @GetMapping("/matches")
    fun getMatchesForSeries(@RequestParam seriesId: String): List<PopulatedMatch> {
        val matches = matchService.findPopulatedBySeriesId(seriesId)
        if (matches == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Series does not exist with ID $seriesId.")
        } else return matches
    }
}