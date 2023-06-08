package net.itsmeeandrew.aoe4esports.service

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MapServiceTest(
    @Autowired val mapService: MapService
) {
    @Test
    fun `returns map by name`() {
        val mapName = "Dry Arabia"
        assertTrue(mapService.findByName(mapName)?.name == mapName)
    }

    @Test
    fun `returns null if not found by name`() {
        val mapName = "Does not exist"
        assertTrue(mapService.findByName(mapName) == null)
    }
}