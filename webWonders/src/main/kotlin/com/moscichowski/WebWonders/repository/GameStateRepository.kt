package com.moscichowski.WebWonders.repository

import com.moscichowski.WebWonders.GameInitialSettings
import com.moscichowski.WebWonders.WondersMapper
import com.moscichowski.wonders.Wonders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class GameStateRepository {
    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var mapper: WondersMapper

    fun storeUser(id: String, name: String) {
        jdbcTemplate.execute("insert into players (id, name) values ('$id', '$name')" +
                "on conflict (id) do update set name = '$name'")
    }

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

    fun getWondersWithUsers(id: String): Triple<Wonders, String, String> {
        println("Getting wonders ${Instant.now()}")

        return jdbcTemplate.query("SELECT gameStates.wonders, p1.name, p2.name FROM gameStates " +
                "JOIN games ON (gameStates.game_id = games.id) " +
                "JOIN players AS p1 ON (p1.id = games.player1) " +
                "LEFT JOIN players AS p2 ON (p2.id = games.player2) " +
                "WHERE gameStates.game_id = '$id'") { rs, _ ->
            Triple(rs.getString(1), rs.getString(2), rs.getString(3))
        }.asSequence().map {
            Triple(mapper.readValue(it.first, Wonders::class.java), it.second, it.third)
        }.last()
    }

    fun getUserGames(playerId: String): List<Triple<String, String, String?>> {
        return jdbcTemplate.query("select games.id, p1.name, p2.name from games " +
                "join players as p1 on (p1.id = games.player1)" +
                "left join players as p2 on (p2.id = games.player2) " +
                "where player1 = '$playerId' or player2 = '$playerId' ") { rs, _ ->
            Triple(rs.getInt(1).toString(), rs.getString(2), rs.getString(3))
        }
    }

    fun saveInvitedUserToGame(playerId: String, inviteCode: String) {
        val update = jdbcTemplate.query("UPDATE games SET player2 = '$playerId' WHERE inviteCode = '$inviteCode' RETURNING player2") { rs, _ ->
            rs.getString(1).toString()
        }
        if (update.count() == 0) { throw Error() }
    }
}