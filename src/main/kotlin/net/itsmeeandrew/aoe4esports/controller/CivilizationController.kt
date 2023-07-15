package net.itsmeeandrew.aoe4esports.controller

import net.itsmeeandrew.aoe4esports.model.Civilization
import net.itsmeeandrew.aoe4esports.service.CivilizationService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["http://localhost:3000"])
class CivilizationController(private val civilizationService: CivilizationService) {

    @GetMapping("/civilizations", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getCivilizations(): List<Civilization> {
        return civilizationService.findAll()
    }
}