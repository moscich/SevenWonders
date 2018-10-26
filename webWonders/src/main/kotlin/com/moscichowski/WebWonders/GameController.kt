package com.moscichowski.WebWonders

import com.fasterxml.jackson.databind.ObjectMapper
import com.moscichowski.wonders.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/games")
class GameController {
    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @RequestMapping(method = [RequestMethod.POST])
    fun postAction(): Any? {
        val wonderList = listOf(
            Wonder("Via Appia", Resource(1,2,3), features = listOf(AddGold(3), RemoveGold, ExtraTurn, VictoryPoints(3))),
            Wonder("Second wonder", Resource(3), features = listOf(DestroyBrownCard)),
            Wonder("Third shieeet", Resource(1,2), features = listOf(DestroySilverCard)),
            Wonder("Everybody dance", Resource(2,glass = 1), features = listOf(ExtraTurn))
        )

        val objectMapper = ObjectMapper()
        val writeValueAsString = objectMapper.writeValueAsString(wonderList)

        return jdbcTemplate.query("insert into games (initial) values ('$writeValueAsString') RETURNING id") { rs, _ ->
            println(rs)
            rs.getInt(1
            )
        }.first()
    }


}