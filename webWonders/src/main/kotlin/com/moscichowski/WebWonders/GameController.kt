package com.moscichowski.WebWonders

import com.moscichowski.WebWonders.service.GameService
import com.moscichowski.wonders.*
import com.moscichowski.wonders.builder.CardBuilder
import com.moscichowski.wonders.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestMapping



@RestController
@CrossOrigin(origins = arrayOf("http://localhost:3000"))//, methods = arrayOf(RequestMethod.OPTIONS, RequestMethod.POST, RequestMethod.GET), allowedHeaders = arrayOf("origin", "content-type", "accept", "x-requested-with"))
@RequestMapping("/games")
class GameController {

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var mapper: WondersMapper

    @Autowired
    lateinit var gameService: GameService

    @RequestMapping(method = [RequestMethod.POST])
    fun postAction(): Any? {
        return gameService.createNewGame()
    }

    @RequestMapping("/{id}", method = [RequestMethod.GET])
    fun getGame(@PathVariable(value="id") id: String): Any {
        return gameService.getGame(id)
    }

    @RequestMapping(value = ["/{gameId}/actions"], method = [RequestMethod.POST])
    fun postAction(@PathVariable(value="gameId") gameId: String, @RequestBody action: Action): Any {
        return gameService.takeAction(gameId, action)
    }

}