package net.itsmeeandrew.aoe4esports.service

import net.itsmeeandrew.aoe4esports.common.TestDBUtils
import net.itsmeeandrew.aoe4esports.model.Series
import org.junit.jupiter.api.Assertions.assertEquals
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
        assertEquals(seriesService.find(TestDBUtils.SERIES)?.id, TestDBUtils.SERIES.id)
    }

    @Test
    fun `updates time`() {
        val newTime = LocalTime.parse("16:00:00")
        seriesService.updateTime(TestDBUtils.SERIES_ID, newTime)

        assertEquals(
            newTime,
            seriesService.find(TestDBUtils.SERIES)?.time
        )

        seriesService.updateTime(TestDBUtils.SERIES_ID, TestDBUtils.SERIES.time!!)
    }

    @Test
    fun `updates date`() {
        val newDate = LocalDate.parse("2022-03-09")
        seriesService.updateDate(TestDBUtils.SERIES_ID, newDate)

        assertEquals(
            newDate,
            seriesService.find(TestDBUtils.SERIES)?.date
        )

        seriesService.updateDate(TestDBUtils.SERIES_ID, TestDBUtils.SERIES.date!!)
    }

    @Test
    fun `updates scores`() {
        seriesService.updateScores(TestDBUtils.SERIES_ID, 2, 4)
        val foundSeries = seriesService.find(TestDBUtils.SERIES)
        assertEquals(foundSeries?.homeScore, 2)
        assertEquals(foundSeries?.awayScore, 4)

        seriesService.updateScores(TestDBUtils.SERIES_ID, TestDBUtils.SERIES.homeScore, TestDBUtils.SERIES.awayScore)
    }

    @Test
    fun `creates and deletes series`() {
        val series = Series(
            TestDBUtils.PLAYER_BEASTY_ID,
            0,
            LocalDate.parse("2022-03-11"),
            null,
            TestDBUtils.PLAYER_MARINELORD_ID,
            4,
            LocalTime.parse("10:00:00"),
            TestDBUtils.TOURNAMENT_ROUND_ID,
            TestDBUtils.TOURNAMENT_ROUND_PHASE_ID
        )

        seriesService.create(series)
        assertTrue(seriesService.find(series) != null)
        assertTrue(seriesService.delete(series))
    }
}