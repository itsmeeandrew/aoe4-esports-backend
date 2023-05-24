package net.itsmeeandrew.aoe4esports.service

import net.itsmeeandrew.aoe4esports.model.Series
import net.itsmeeandrew.aoe4esports.repository.SeriesRepository
import org.springframework.stereotype.Service

@Service
class SeriesService(private val seriesRepository: SeriesRepository) {
    fun create(series: Series): Series? {
        val existingSeries = find(series)
        return if (existingSeries == null) {
            seriesRepository.create(series)
        } else {
            println("Series already exists in database.")
            existingSeries
        }
    }

    // TODO: Rename to findOne
    fun find(series: Series): Series? {
        return seriesRepository.find(series)
    }
}
