package com.moscichowski.WebWonders

import com.moscichowski.WebWonders.common.GameCreated
import com.moscichowski.WebWonders.security.OAuth2
import com.moscichowski.WebWonders.service.GameService
import com.moscichowski.wonders.Action
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import javax.servlet.http.HttpServletRequest

@RestController
@CrossOrigin(origins = ["http://localhost:3000"])//, methods = arrayOf(RequestMethod.OPTIONS, RequestMethod.POST, RequestMethod.GET), allowedHeaders = arrayOf("origin", "content-type", "accept", "x-requested-with"))
@RequestMapping("/games")
class GameController {

    @Autowired
    lateinit var restTemplate: RestTemplate

    @Autowired
    lateinit var gameService: GameService

    @Autowired
    lateinit var auth: OAuth2

    @RequestMapping(method = [RequestMethod.GET])
    fun returnSomeGames(request: HttpServletRequest): Any? {
        val header = request.getHeader("Authorization") ?: throw InvalidTokenError()
        val token = header.slice(7 until header.length)
        val userForToken = auth.getUserForToken(token)
        return listOf("1", "dwa", "three")
    }

    @RequestMapping(method = [RequestMethod.POST])
    fun postAction(request: HttpServletRequest): GameCreated? {
        val header = request.getHeader("Authorization") ?: throw InvalidTokenError()
        val token = header.slice(7 until header.length)
        val userForToken = auth.getUserForToken(token)
        return GameCreated(gameService.createNewGame(), "xd")
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