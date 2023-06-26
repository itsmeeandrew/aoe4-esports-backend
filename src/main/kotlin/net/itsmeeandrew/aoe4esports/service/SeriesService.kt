package net.itsmeeandrew.aoe4esports.service

import net.itsmeeandrew.aoe4esports.model.PopulatedSeries
import net.itsmeeandrew.aoe4esports.model.Series
import net.itsmeeandrew.aoe4esports.repository.SeriesRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime

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

    fun find(series: Series): Series? {
        return seriesRepository.find(series)
    }

    fun findLatest(n: Int): List<PopulatedSeries> {
        return seriesRepository.findLatest(n)
    }

    fun findById(id: String): PopulatedSeries? {
        val parsedId = id.toIntOrNull()
        if (parsedId != null) {
            return seriesRepository.findById(parsedId)
        }

        return null
    }

    fun updateScores(id: Int, homeScore: Int, awayScore: Int): Boolean {
        return seriesRepository.updateScores(id, homeScore, awayScore)
    }

    fun updateTime(id: Int, time: LocalTime): Boolean {
        return seriesRepository.updateTime(id, time)
    }

    fun updateDate(id: Int, date: LocalDate): Boolean {
        return seriesRepository.updateDate(id, date)
    }

    fun delete(series: Series): Boolean {
        return seriesRepository.delete(series)
    }
}
