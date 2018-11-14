package com.moscichowski.WebWonders

import com.moscichowski.WebWonders.common.GameCreated
import com.moscichowski.WebWonders.repository.MyMessageHandler
import com.moscichowski.WebWonders.security.OAuth2
import com.moscichowski.WebWonders.service.GameService
import com.moscichowski.wonders.Action
import com.moscichowski.wonders.Game
import com.moscichowski.wonders.Wonders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.socket.WebSocketHandler
import java.time.Instant
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

    @Autowired
    lateinit var webSocketHandler: MyMessageHandler

    data class GameInList(val id: String, val player1: String, val player2: String?)
    @RequestMapping(method = [RequestMethod.GET])
    fun returnSomeGames(request: HttpServletRequest): List<GameInList> {
        return gameService.getGames(getUser(request)).map { GameInList(it.first, it.second, it.third) }
    }

    @RequestMapping(method = [RequestMethod.POST])
    fun createGame(request: HttpServletRequest): GameCreated? {
        val (gameId, inviteCode) = gameService.createNewGame(getUser(request))
        return GameCreated(gameId, inviteCode)
    }

    data class GameResponse(val game: Game, val player1: String, val player2: String?)
    @RequestMapping("/{id}", method = [RequestMethod.GET])
    fun getGame(@PathVariable(value="id") id: String): Any {
        val (wonders, player1, player2) = gameService.getGame(id)
        return GameResponse(wonders.game, player1, player2)
    }

    @RequestMapping(method = [RequestMethod.PUT])
    fun joinGame(request: HttpServletRequest, @RequestBody inviteCode: Map<String, String>): Any {
        val header = request.getHeader("Authorization") ?: throw InvalidTokenError()
        val token = header.slice(7 until header.length)
        val userForToken = auth.getUserIdForToken(token)
        gameService.joinGame(userForToken, inviteCode["inviteCode"]!!)
        return "xd"
    }

    @RequestMapping(value = ["/{gameId}/actions"], method = [RequestMethod.POST])
    fun postAction(request: HttpServletRequest, @PathVariable(value="gameId") gameId: String, @RequestBody action: Action): Any {
        val start = Instant.now()
        val header = request.getHeader("Authorization") ?: throw InvalidTokenError()
        val token = header.slice(7 until header.length)
        val userForToken = auth.getUserIdForToken(token)

        val (wonders, player1, player2) = gameService.takeAction(gameId, action, userForToken)
        Thread {
            webSocketHandler.notify(gameId)
        }.run()
        println("Instant.now() - start = ${Instant.now().toEpochMilli() - start.toEpochMilli()}")
        return GameResponse(wonders.game, player1, player2)

    }

    private fun getUser(request: HttpServletRequest): String {
        val header = request.getHeader("Authorization") ?: throw InvalidTokenError()
        val token = header.slice(7 until header.length)
        return auth.getUserIdForToken(token)
    }
}

data class InviteCodeRequest(val inviteCode:String)