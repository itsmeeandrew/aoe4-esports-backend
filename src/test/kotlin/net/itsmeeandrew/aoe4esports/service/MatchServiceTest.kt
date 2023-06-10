package net.itsmeeandrew.aoe4esports.service

import net.itsmeeandrew.aoe4esports.common.TestDBUtils
import net.itsmeeandrew.aoe4esports.model.Match
import org.junit.jupiter.api.Assertions.assertTrue
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
    fun `creates and deletes match`() {
        val createdMatch = matchService.create(
            Match(
                3, 5, null, TestDBUtils.MAP_DRY_ARABIA_ID, TestDBUtils.SERIES_ID, TestDBUtils.PLAYER_BEASTY_ID
            )
        )

        assertTrue(createdMatch != null)
        assertTrue(matchService.deleteMany(listOf(createdMatch!!)))
    }

    @Test
    fun `returns matches by series id`() {
        val matches = matchService.findBySeriesId(TestDBUtils.SERIES_ID)
        assertTrue(matches.isNotEmpty())
    }
}