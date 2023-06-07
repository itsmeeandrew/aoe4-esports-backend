package net.itsmeeandrew.aoe4esports.service

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource(
    locations = ["classpath:application-test.properties"]
)
class TournamentServiceTest(
    @Autowired val tournamentService: TournamentService
) {
    @Test
    fun `finds all`() {
        val foundTournaments = tournamentService.findAll()
        assert(foundTournaments.isNotEmpty())
    }
}