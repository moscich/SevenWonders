package com.moscichowski.WebWonders.service

import com.moscichowski.WebWonders.ForbiddenError
import com.moscichowski.WebWonders.GameInitialSettings
import com.moscichowski.WebWonders.repository.GameStateRepository
import com.moscichowski.wonders.Action
import com.moscichowski.wonders.Game
import com.moscichowski.wonders.WondersBuilder
import com.moscichowski.wonders.builder.CardBuilder
import com.moscichowski.wonders.builder.WonderBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GameService {

    @Autowired
    lateinit var repo: GameStateRepository

    fun createNewGame(playerId: String, invitationCode: String): String {

        val wonderBuilder = WonderBuilder()
        val wonderList = wonderBuilder.getWonders()
        val cardBuilder = CardBuilder()
        val cards = listOf(
                cardBuilder.getCards(0).subList(0, 20),
                cardBuilder.getCards(1).subList(0, 20),
                cardBuilder.getCards(1).subList(0, 20))


        //TODO shuffle

        val wonders = WondersBuilder().setupWonders(wonderList.subList(0, 8), cards)
        val gameId = repo.storeInitialState(GameInitialSettings(wonderList, cards), playerId, invitationCode).toString()
        repo.storeWonders(gameId, wonders)
        return gameId
    }

    fun takeAction(gameId: String, action: Action, userId: String): Game {
        val wondersWrapped = repo.getWonders(gameId)
        val wonders = wondersWrapped.first
        if ((wonders.game.currentPlayer == 0 && userId == wondersWrapped.second)
            || (wonders.game.currentPlayer == 1 && userId == wondersWrapped.third)) {
            wonders.takeAction(action)
            repo.storeWonders(gameId, wonders)
            return getGame(gameId)
        } else {
            throw ForbiddenError()
        }
    }

    fun joinGame(gameId: String, userId: String, invitationCode: String) {
        repo.storeInvitation(gameId, userId, invitationCode)
    }

    fun getGame(id: String): Game {
        return repo.getWonders(id).first.game
    }
}