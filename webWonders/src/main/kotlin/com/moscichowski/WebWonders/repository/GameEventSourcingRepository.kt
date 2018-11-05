package com.moscichowski.WebWonders.repository

import com.moscichowski.WebWonders.GameInitialSettings
import com.moscichowski.WebWonders.WondersMapper
import com.moscichowski.wonders.Action
import com.moscichowski.wonders.Wonders
import com.moscichowski.wonders.WondersBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class GameEventSourcingRepository {
    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var mapper: WondersMapper

    fun storeInitialState(settings: GameInitialSettings): Int {
        val writeValueAsString = mapper.writeValueAsString(settings)

        return jdbcTemplate.query("insert into games (initial) values ('$writeValueAsString') RETURNING id") { rs, _ ->
            rs.getInt(1)
        }.first()
    }

    fun storeWonders(gameId: String, wonders: Wonders): String {
        val deserializedAction = mapper.writeValueAsString(wonders)
        return jdbcTemplate.query("insert into gameStates (game_id, wonders) values ('$gameId', '$deserializedAction') RETURNING id") { rs, _ ->
            rs.getInt(1).toString()
        }.first()
    }

    fun getWonders(id: String): Wonders {
        return jdbcTemplate.query("select wonders from gameStates where game_id = $id") { rs, _ ->
            rs.getString(1)
        }.map {
            println("it = ${it}")
            mapper.readValue(it, Wonders::class.java)
        }.last()
    }

    fun storeAction(gameId: String, action: Action) {
        val deserializedAction = mapper.writeValueAsString(action)
        jdbcTemplate.execute("insert into actions (game_id, action) values ('$gameId', '$deserializedAction')")
    }

    fun getWondersGame(id: String): Wonders {
        val initialGameState = jdbcTemplate.query("select * from games where id = $id") { rs, _ ->
            rs.getString(2)
        }.first()

        val readValue: GameInitialSettings = mapper.readValue(initialGameState, GameInitialSettings::class.java)

        val wonders = WondersBuilder().setupWonders(readValue.wonders, readValue.cards)

        val actions = jdbcTemplate.query("select action from actions where game_id = $id") { rs, _ ->
            rs.getString(1)
        }.map { mapper.readValue(it, Action::class.java) }

        actions.forEach { wonders.takeAction(it) }

        return wonders
    }

}