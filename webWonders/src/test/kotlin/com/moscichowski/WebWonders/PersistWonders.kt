package com.moscichowski.WebWonders

import com.moscichowski.WebWonders.repository.GameEventSourcingRepository
import com.moscichowski.wonders.WondersBuilder
import com.moscichowski.wonders.builder.CardBuilder
import com.moscichowski.wonders.builder.WonderBuilder
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
    lateinit var repo: GameEventSourcingRepository

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

        val gameId = repo.storeInitialState(GameInitialSettings(wonderList, cards)).toString()

        repo.storeWonders(gameId, wonders)
        val retrievedWonders = repo.getWonders(gameId)
        assertEquals(wonders.game, retrievedWonders.game)
        assertEquals(wonders.cards, retrievedWonders.cards)
    }
}