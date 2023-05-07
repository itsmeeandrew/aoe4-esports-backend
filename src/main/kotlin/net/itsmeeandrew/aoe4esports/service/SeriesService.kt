package net.itsmeeandrew.aoe4esports.service

import net.itsmeeandrew.aoe4esports.model.Series
import net.itsmeeandrew.aoe4esports.repository.SeriesRepository
import org.springframework.stereotype.Service

@Service
class SeriesService(private val seriesRepository: SeriesRepository) {
    fun createSeries(series: Series): Series? {
        return seriesRepository.addSeries(series)
    }
}
