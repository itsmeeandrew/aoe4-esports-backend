package net.itsmeeandrew.aoe4esports.controller

import net.itsmeeandrew.aoe4esports.model.PopulatedSeries
import net.itsmeeandrew.aoe4esports.service.SeriesService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api")
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