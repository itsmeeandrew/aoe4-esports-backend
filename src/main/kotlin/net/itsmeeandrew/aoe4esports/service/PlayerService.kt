package net.itsmeeandrew.aoe4esports.service

import net.itsmeeandrew.aoe4esports.model.Player
import net.itsmeeandrew.aoe4esports.repository.PlayerRepository
import org.springframework.stereotype.Service

@Service
class PlayerService(private val playerRepository: PlayerRepository) {
    fun findById(id: Int): Player? {
        return playerRepository.findById(id)
    }

    fun findByName(name: String): Player? {
        return playerRepository.findByName(name)
    }

    fun create(player: Player): Player? {
        if (player.name.isBlank()) {
            return null
        }
        return playerRepository.create(player)
    }

    fun findByNameOrCreate(name: String): Player? {
        return findByName(name) ?: create(Player(null, name))
    }
}