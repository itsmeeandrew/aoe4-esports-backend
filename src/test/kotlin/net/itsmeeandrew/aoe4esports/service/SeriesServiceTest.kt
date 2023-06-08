package net.itsmeeandrew.aoe4esports.service

import net.itsmeeandrew.aoe4esports.model.Series
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import java.time.LocalDate
import java.time.LocalTime

@SpringBootTest
@TestPropertySource(
    locations = ["classpath:application-test.properties"]
)
class SeriesServiceTest(
    @Autowired private val seriesService: SeriesService
) {
    @Test
    fun `finds one`() {
        val seriesToFind = Series(2, 0, null, null, 1, 2, null, "Golden League/1/Round/1", 1)
        val foundSeries = seriesService.find(seriesToFind)
        assertTrue(foundSeries?.id == 1)
    }

    @Test
    fun `updates time`() {
        val seriesToFind = Series(2, 0, null, null, 1, 2, null, "Golden League/1/Round/1", 1)
        val foundSeries = seriesService.find(seriesToFind)
        val newTime = LocalTime.parse("16:00:00")
        seriesService.updateTime(foundSeries?.id!!, newTime)
        val updatedSeries = seriesService.find(seriesToFind)
        assertTrue(updatedSeries?.time?.equals(newTime) ?: false)

        seriesService.updateTime(foundSeries.id!!, foundSeries.time!!)
    }

    @Test
    fun `updates date`() {
        val seriesToFind = Series(2, 0, null, null, 1, 2, null, "Golden League/1/Round/1", 1)
        val foundSeries = seriesService.find(seriesToFind)
        val newDate = LocalDate.parse("2023-01-01")
        seriesService.updateDate(foundSeries?.id!!, newDate)
        val updatedSeries = seriesService.find(seriesToFind)
        assertTrue(updatedSeries?.date?.equals(newDate) ?: false)

        seriesService.updateDate(foundSeries.id!!, foundSeries.date!!)
    }
}