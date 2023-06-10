package net.itsmeeandrew.aoe4esports.service

import net.itsmeeandrew.aoe4esports.model.GMap
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource(
    locations = ["classpath:application-test.properties"]
)
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

    @Test
    fun `creates and deletes map`() {
        val mapName = "Baltic"
        assertTrue(mapService.create(GMap(null, mapName)) != null)
        assertTrue(mapService.deleteByName(mapName))
    }
}