package net.itsmeeandrew.aoe4esports.controller

import net.itsmeeandrew.aoe4esports.model.PopulatedSeries
import net.itsmeeandrew.aoe4esports.service.SeriesService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["http://localhost:3000"])
class SeriesController(private val seriesService: SeriesService) {

    @GetMapping("/series/latest")
    fun getLatestSeries(): List<PopulatedSeries> {
        return seriesService.findLatest(5)
    }

    @GetMapping("/series/{id}")
    fun getSeriesById(@PathVariable id: String): PopulatedSeries {
        val series = seriesService.findById(id)
        if (series == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Series does not exist with ID $id.")
        } else return series
    }
}