package net.itsmeeandrew.aoe4esports.service

import net.itsmeeandrew.aoe4esports.model.GMap
import net.itsmeeandrew.aoe4esports.repository.MapRepository
import org.springframework.stereotype.Service

@Service
class MapService(private val mapRepository: MapRepository) {
    fun findByName(name: String): GMap? = mapRepository.findByName(name)

    fun add(map: GMap): GMap? = mapRepository.create(map)
}