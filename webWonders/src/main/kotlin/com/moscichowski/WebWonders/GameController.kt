package com.moscichowski.WebWonders

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.moscichowski.wonders.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.io.File


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
            Wonder("Second wonder", Resource(3), features = listOf(DestroyBrownCard)),
            Wonder("Third shieeet", Resource(1,2), features = listOf(DestroySilverCard)),
            Wonder("Everybody dance", Resource(2,glass = 1), features = listOf(ExtraTurn)),
            Wonder("Via Appia 2", Resource(1,2,3), features = listOf(AddGold(3), RemoveGold, ExtraTurn, VictoryPoints(3))),
            Wonder("Second wonder 2", Resource(3), features = listOf(DestroyBrownCard)),
            Wonder("Third shieeet 2", Resource(1,2), features = listOf(DestroySilverCard)),
            Wonder("Everybody dance 2", Resource(2,glass = 1), features = listOf(ExtraTurn))
        )

        val writeValueAsString = mapper.writeValueAsString(wonderList)

        return jdbcTemplate.query("insert into games (initial) values ('$writeValueAsString') RETURNING id") { rs, _ ->
            rs.getInt(1)
        }.first()
    }

    @RequestMapping("/{id}", method = [RequestMethod.GET])
    fun getGame(@PathVariable(value="id") id: Int): Any {
        val first = jdbcTemplate.query("select * from games where id = $id") { rs, _ ->
            rs.getString(2)
        }.first()
//        val type: Type = object : TypeToken<ServiceCall<SurveyListModel>>() {}.type

//        val actuallyFoos: List<Wonder> = mapper.readValue(
//                File("/your/path/test.json"), object : TypeReference<List<Wonder>>() {
//
//        })
//
        val readValue: List<Wonder> = mapper.readValue(first, object: TypeReference<List<Wonder>>() {})
        val game = Game(Board(listOf()), readValue)
        Wonders(game)
//        val convertValue = mapper.convertValue(first, List::class.java)
        return game
    }

}