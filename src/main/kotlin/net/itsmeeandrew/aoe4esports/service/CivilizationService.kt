package net.itsmeeandrew.aoe4esports.service

import net.itsmeeandrew.aoe4esports.model.Civilization
import net.itsmeeandrew.aoe4esports.repository.CivilizationRepository
import org.springframework.stereotype.Service

@Service
class CivilizationService(private val civilizationRepository: CivilizationRepository) {

    fun getCivilizations(): List<Civilization> = civilizationRepository.findAll();

    fun findByName(name: String): Civilization? = civilizationRepository.findByName(name)
}