package net.itsmeeandrew.aoe4esports.service

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CivilizationServiceTest(
    @Autowired val civilizationService: CivilizationService
) {
    @Test
    fun `returns all civilizations`() {
        assert(civilizationService.findAll().size == 10)
    }

    @Test
    fun `returns civilization by name`() {
        val civilizationName = "Rus"
        assert(civilizationService.findByName(civilizationName)?.name == civilizationName)
    }

    @Test
    fun `returns null if not found by name`() {
        val civilizationName = "Does not exist"
        assert(civilizationService.findByName(civilizationName) == null)
    }
}