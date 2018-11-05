package com.moscichowski.WebWonders.service

import com.moscichowski.WebWonders.GameInitialSettings
import com.moscichowski.WebWonders.repository.GameRepository
import com.moscichowski.wonders.Action
import com.moscichowski.wonders.Game
import com.moscichowski.wonders.builder.CardBuilder
import com.moscichowski.wonders.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GameService {

    @Autowired
    lateinit var repo: GameRepository

    fun createNewGame(): Int {
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

        return repo.storeInitialState(GameInitialSettings(wonderList, cards))
    }

    fun takeAction(gameId: String, action: Action) {
        val wonders = repo.getWondersGame(gameId)
        wonders.takeAction(action)
        repo.storeAction(gameId, action)
    }

    fun getGame(id: String): Game {
        return repo.getWondersGame(id).game
    }
}