package com.moscichowski.WebWonders

import com.fasterxml.jackson.databind.ObjectMapper
import com.moscichowski.wonders.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import java.sql.PreparedStatement
import com.oracle.util.Checksums.update
import org.springframework.web.bind.annotation.RestController
import org.postgresql.util.PGobject




@RestController
@RequestMapping("/games")
class GameController {
    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @RequestMapping(method = [RequestMethod.POST])
    fun postAction(): String {
        val wonderList = listOf(
            Wonder("First wonder", Resource(1,2,3), features = listOf(Customs)),
            Wonder("Second wonder", Resource(3), features = listOf(DestroyBrownCard)),
            Wonder("Third shieeet", Resource(1,2), features = listOf(DestroySilverCard)),
            Wonder("Everybody dance", Resource(2,glass = 1), features = listOf(ExtraTurn))
        )

        val objectMapper = ObjectMapper()
        val writeValueAsString = objectMapper.writeValueAsString(wonderList)

        val jsonbObj = PGobject()
        jsonbObj.type = "json"
        jsonbObj.value = "{\"Hehe\" : \"Num\"}"

        jdbcTemplate.batchUpdate("insert into games (initial) values ('$writeValueAsString')")

        return "ok"
    }


}