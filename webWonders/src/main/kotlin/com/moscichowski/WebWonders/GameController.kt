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
        val inviteCode = "xd"
        return GameCreated(gameService.createNewGame(userForToken, inviteCode), inviteCode)
    }

    @RequestMapping("/{id}", method = [RequestMethod.GET])
    fun getGame(@PathVariable(value="id") id: String): Any {
        return gameService.getGame(id)
    }

    @RequestMapping("/{id}", method = [RequestMethod.PUT])
    fun joinGame(request: HttpServletRequest, @PathVariable(value="id") id: String, @RequestBody inviteCode: Map<String, String>): Any {
        val header = request.getHeader("Authorization") ?: throw InvalidTokenError()
        val token = header.slice(7 until header.length)
        val userForToken = auth.getUserForToken(token)
        gameService.joinGame(id, userForToken, inviteCode["inviteCode"]!!)
        return "xd"
    }

    @RequestMapping(value = ["/{gameId}/actions"], method = [RequestMethod.POST])
    fun postAction(request: HttpServletRequest, @PathVariable(value="gameId") gameId: String, @RequestBody action: Action): Any {
        val header = request.getHeader("Authorization") ?: throw InvalidTokenError()
        val token = header.slice(7 until header.length)
        val userForToken = auth.getUserForToken(token)

        return gameService.takeAction(gameId, action, userForToken)
    }
}

data class InviteCodeRequest(val inviteCode:String)