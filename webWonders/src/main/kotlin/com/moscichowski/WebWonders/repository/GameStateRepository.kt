package com.moscichowski.WebWonders.repository

import com.moscichowski.WebWonders.GameInitialSettings
import com.moscichowski.WebWonders.WondersMapper
import com.moscichowski.wonders.Wonders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class GameStateRepository {
    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var mapper: WondersMapper

    fun storeInitialState(settings: GameInitialSettings, player1: String, inviteCode: String): Int {
        val writeValueAsString = mapper.writeValueAsString(settings)

        return jdbcTemplate.query("insert into games (initial, player1, inviteCode) values ('$writeValueAsString', '$player1', '$inviteCode') RETURNING id") { rs, _ ->
            rs.getInt(1)
        }.first()
    }

    fun storeWonders(gameId: String, wonders: Wonders): String {
        val deserializedAction = mapper.writeValueAsString(wonders)
        return jdbcTemplate.query("insert into gameStates (game_id, wonders) values ('$gameId', '$deserializedAction') RETURNING id") { rs, _ ->
            rs.getInt(1).toString()
        }.first()
    }

    fun getWonders(id: String): Triple<Wonders, String, String> {
        return jdbcTemplate.query("select gameStates.wonders, games.player1, games.player2 from gameStates JOIN games ON (gameStates.game_id = games.id) where gameStates.game_id = $id") { rs, _ ->
            Triple(rs.getString(1), rs.getString(2), rs.getString(3))
        }.asSequence().map {
            Triple(mapper.readValue(it.first, Wonders::class.java), it.second, it.third)
        }.last()
    }

    fun storeInvitation(gameId: String, playerId: String, inviteCode: String) {
        val update = jdbcTemplate.query("UPDATE games SET player2 = '$playerId' WHERE id = '$gameId' AND inviteCode = '$inviteCode' RETURNING player2") { rs, _ ->
            rs.getString(1).toString()
        }
        if (update.count() == 0) { throw Error() }
    }
}