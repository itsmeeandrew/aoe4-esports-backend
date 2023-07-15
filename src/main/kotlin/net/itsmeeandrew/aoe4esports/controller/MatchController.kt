package net.itsmeeandrew.aoe4esports.controller

import net.itsmeeandrew.aoe4esports.model.PopulatedMatch
import net.itsmeeandrew.aoe4esports.service.MatchService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["http://localhost:3000"])
class MatchController(private val matchService: MatchService) {

    @GetMapping("/matches")
    fun getMatchesForSeries(@RequestParam seriesId: String): List<PopulatedMatch> {
        val matches = matchService.findPopulatedBySeriesId(seriesId)
        if (matches == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Series does not exist with ID $seriesId.")
        } else return matches
    }
}