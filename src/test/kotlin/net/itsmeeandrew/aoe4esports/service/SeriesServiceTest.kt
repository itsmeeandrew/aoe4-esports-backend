package net.itsmeeandrew.aoe4esports.service

import net.itsmeeandrew.aoe4esports.model.Series
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource(
    locations = ["classpath:application-test.properties"]
)
class SeriesServiceTest(
    @Autowired private val seriesService: SeriesService
) {
    @Test
    fun `finds one`() {
        val seriesToFind =Series(2, 0, null, null, 1, 2, null, "Golden League/1/Round/1", 1)
        val foundSeries = seriesService.find(seriesToFind)
        assert(foundSeries?.id == 1)
    }
}