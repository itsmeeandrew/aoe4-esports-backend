package net.itsmeeandrew.aoe4esports.controller

import net.itsmeeandrew.aoe4esports.model.PopulatedSeries
import net.itsmeeandrew.aoe4esports.service.SeriesService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class SeriesController(private val seriesService: SeriesService) {

    @GetMapping("/series/latest")
    fun getLatestSeries(): List<PopulatedSeries> {
        return seriesService.findLatest(5)
    }
}