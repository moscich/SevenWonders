package com.moscichowski.WebWonders.service

import com.moscichowski.WebWonders.GameInitialSettings
import com.moscichowski.WebWonders.repository.GameEventSourcingRepository
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
    lateinit var repo: GameEventSourcingRepository

    fun createNewGame(): String {

        val wonderBuilder = WonderBuilder()
        val wonderList = wonderBuilder.getWonders()
        val cardBuilder = CardBuilder()
        val cards = listOf(
                cardBuilder.getCards(0).subList(0, 20),
                cardBuilder.getCards(1).subList(0, 20),
                cardBuilder.getCards(1).subList(0, 20))


        //TODO shuffle

        val wonders = WondersBuilder().setupWonders(wonderList.subList(0, 8), cards)
        val gameId = repo.storeInitialState(GameInitialSettings(wonderList, cards)).toString()
        repo.storeWonders(gameId, wonders)
        return gameId
    }

    fun takeAction(gameId: String, action: Action): Game {
        val wonders = repo.getWonders(gameId)
        wonders.takeAction(action)
        repo.storeWonders(gameId, wonders)
        return getGame(gameId)
    }

    fun getGame(id: String): Game {
        return repo.getWonders(id).game
    }
}