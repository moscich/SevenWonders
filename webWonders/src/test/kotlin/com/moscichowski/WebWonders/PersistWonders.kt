package com.moscichowski.WebWonders

import com.moscichowski.WebWonders.repository.GameRepository
import com.moscichowski.WebWonders.repository.GameStateStoreRepository
import com.moscichowski.wonders.WondersBuilder
import com.moscichowski.wonders.builder.CardBuilder
import com.moscichowski.wonders.builder.WonderBuilder
import com.moscichowski.wonders.model.*
import org.flywaydb.test.annotation.FlywayTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@FlywayTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner::class)
class PersistWonders {

    @Autowired
    lateinit var repo: GameStateStoreRepository

    @Autowired
    lateinit var createGameRepo: GameRepository

    @Test
    fun storeWonders() {
        val wonderBuilder = WonderBuilder()
        val cardBuilder = CardBuilder()
        val wonderList = wonderBuilder.getWonders()
        val cards = listOf(
                cardBuilder.getCards().subList(0, 20),
                cardBuilder.getCards().subList(0, 20),
                cardBuilder.getCards().subList(0, 20))

        val wonders = WondersBuilder().setupWonders(wonderList.subList(0, 8), cards)

        val gameId = createGameRepo.storeInitialState(GameInitialSettings(wonderList, cards)).toString()

        val id = repo.storeWonders(gameId, wonders)
        val retrievedWonders = repo.getWonders(id)
        assertEquals(wonders.game, retrievedWonders.game)
        assertEquals(wonders.cards, retrievedWonders.cards)
    }
}