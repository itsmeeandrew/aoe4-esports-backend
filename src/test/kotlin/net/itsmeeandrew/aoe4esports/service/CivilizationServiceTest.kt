package net.itsmeeandrew.aoe4esports.service

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CivilizationServiceTest(
    @Autowired val civilizationService: CivilizationService
) {
    @Test
    fun `returns all civilizations`() {
        assertTrue(civilizationService.findAll().size == 10)
    }

    @Test
    fun `returns civilization by name`() {
        val civilizationName = "Rus"
        assertTrue(civilizationService.findByName(civilizationName)?.name == civilizationName)
    }

    @Test
    fun `returns null if not found by name`() {
        val civilizationName = "Does not exist"
        assertTrue(civilizationService.findByName(civilizationName) == null)
    }
}