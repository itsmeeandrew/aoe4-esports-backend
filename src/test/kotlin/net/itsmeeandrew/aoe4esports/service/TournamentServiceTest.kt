package net.itsmeeandrew.aoe4esports.service

import net.itsmeeandrew.aoe4esports.common.TestDBUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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
        assertTrue(foundTournaments.isNotEmpty())
    }

    @Test
    fun `creates, finds and deletes ongoing`() {
        val createdTournament = tournamentService.create(TestDBUtils.ONGOING_TOURNAMENT)
        assertTrue(createdTournament != null)

        val ongoingTournaments = tournamentService.findOngoing()
        assertEquals(ongoingTournaments[0].id, TestDBUtils.ONGOING_TOURNAMENT.id)
        assertTrue(ongoingTournaments.size == 1)

        assertTrue(tournamentService.deleteById(createdTournament!!.id))
    }

    @Test
    fun `finds tournament by id`() {
        val foundTournament = tournamentService.findById(TestDBUtils.TOURNAMENT_ID)
        assertEquals(TestDBUtils.TOURNAMENT_ID, foundTournament?.id)
    }

    @Test
    fun `gets maps for tournament`() {
        val maps = tournamentService.getMaps(TestDBUtils.TOURNAMENT_ID)
        assertTrue(maps.isNotEmpty())
    }
}