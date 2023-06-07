package net.itsmeeandrew.aoe4esports.service

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
        assert(mapService.findByName(mapName)?.name == mapName)
    }

    @Test
    fun `returns null if not found by name`() {
        val mapName = "Does not exist"
        assert(mapService.findByName(mapName) == null)
    }
}