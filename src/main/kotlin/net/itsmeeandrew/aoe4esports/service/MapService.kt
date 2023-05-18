package net.itsmeeandrew.aoe4esports.service

import net.itsmeeandrew.aoe4esports.model.GMap
import net.itsmeeandrew.aoe4esports.repository.MapRepository
import org.springframework.stereotype.Service

@Service
class MapService(private val mapRepository: MapRepository) {
    fun findByName(name: String): GMap? {
        return mapRepository.findByName(name)
    }

    fun create(map: GMap): GMap? {
        if (map.name.isBlank()) {
            return null
        }
        return mapRepository.create(map)
    }

    fun findByNameOrCreate(name: String): GMap? {
        return findByName(name) ?: create(GMap(null, name))
    }
}