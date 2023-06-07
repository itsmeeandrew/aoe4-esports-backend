package net.itsmeeandrew.aoe4esports.service

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource(
    locations = ["classpath:application-test.properties"]
)
class MatchServiceTest (
    @Autowired val matchService: MatchService
) {
    @Test
    fun `returns matches by series id`() {
        val matches = matchService.findBySeriesId(505)
        assert(matches.all { m -> m.seriesId == 505 })
    }
}