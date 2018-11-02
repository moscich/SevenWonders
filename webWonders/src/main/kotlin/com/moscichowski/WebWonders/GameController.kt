package com.moscichowski.WebWonders

import com.fasterxml.jackson.core.type.TypeReference
import com.moscichowski.wonders.*
import com.moscichowski.wonders.builder.CardBuilder
import com.moscichowski.wonders.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/games")
class GameController {

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var mapper: WondersMapper

    @RequestMapping(method = [RequestMethod.POST])
    fun postAction(): Any? {
        val wonderList = listOf(
            Wonder("Via Appia", Resource(1,2,3), features = listOf(AddGold(3), RemoveGold, ExtraTurn, VictoryPoints(3))),
            Wonder("Second wonders", Resource(3), features = listOf(DestroyBrownCard)),
            Wonder("Third shieeet", Resource(1,2), features = listOf(DestroySilverCard)),
            Wonder("Everybody dance", Resource(2,glass = 1), features = listOf(ExtraTurn)),
            Wonder("Via Appia 2", Resource(1,2,3), features = listOf(AddGold(3), RemoveGold, ExtraTurn, VictoryPoints(3))),
            Wonder("Second wonders 2", Resource(3), features = listOf(DestroyBrownCard)),
            Wonder("Third shieeet 2", Resource(1,2), features = listOf(DestroySilverCard)),
            Wonder("Everybody dance 2", Resource(2,glass = 1), features = listOf(ExtraTurn))
        )

        val cardBuilder = CardBuilder()
        val cards = listOf(
                cardBuilder.getCards().subList(0, 20),
                cardBuilder.getCards().subList(0, 20),
                cardBuilder.getCards().subList(0, 20))


        //TODO shuffle

        val writeValueAsString = mapper.writeValueAsString(GameInitialSettings(wonderList, cards))

        return jdbcTemplate.query("insert into games (initial) values ('$writeValueAsString') RETURNING id") { rs, _ ->
            rs.getInt(1)
        }.first()
    }

    @RequestMapping("/{id}", method = [RequestMethod.GET])
    fun getGame(@PathVariable(value="id") id: Int): Any {
        val first = jdbcTemplate.query("select * from games where id = $id") { rs, _ ->
            rs.getString(2)
        }.first()

        val readValue: GameInitialSettings = mapper.readValue(first, GameInitialSettings::class.java)
        val game = Game(readValue.wonders, readValue.cards)
        val wonders = Wonders(game)

        val actions = jdbcTemplate.query("select action from actions where game_id = $id") { rs, _ ->
            rs.getString(1)
        }.map { mapper.readValue(it, Action::class.java) }

        println("actions = ${actions}")
        actions.forEach { wonders.takeAction(it) }

        return game
    }

    @RequestMapping(value = ["/{gameId}/actions"], method = [RequestMethod.POST])
    fun postAction(@PathVariable(value="gameId") gameId: Int, @RequestBody action: Action): Any {
        val first = jdbcTemplate.query("select * from games where id = $gameId") { rs, _ ->
            rs.getString(2)
        }.first()


        val readValue: GameInitialSettings = mapper.readValue(first, GameInitialSettings::class.java)
        val game = Game(readValue.wonders, readValue.cards)
        val wonders = Wonders(game)

        val actions = jdbcTemplate.query("select action from actions where game_id = $gameId") { rs, _ ->
            rs.getString(1)
        }.map { mapper.readValue(it, Action::class.java) }

        println("actions = ${actions}")
        actions.forEach { wonders.takeAction(it) }
        wonders.takeAction(action)

        val deserializedAction = mapper.writeValueAsString(action)
        jdbcTemplate.execute("insert into actions (game_id, action) values ('$gameId', '$deserializedAction')")

        return game
    }

}