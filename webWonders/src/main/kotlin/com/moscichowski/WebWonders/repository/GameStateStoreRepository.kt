package com.moscichowski.WebWonders.repository

import com.moscichowski.WebWonders.WondersMapper
import com.moscichowski.wonders.Action
import com.moscichowski.wonders.Wonders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class GameStateStoreRepository {
    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var mapper: WondersMapper

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
            mapper.readValue(it, Wonders::class.java)
        }.first()
    }
}