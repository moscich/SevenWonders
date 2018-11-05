package com.moscichowski.WebWonders

import com.moscichowski.WebWonders.repository.GameRepository
import com.moscichowski.WebWonders.repository.GameStateStoreRepository
import com.moscichowski.wonders.Wonders
import com.moscichowski.wonders.WondersBuilder
import com.moscichowski.wonders.builder.CardBuilder
import com.moscichowski.wonders.model.*
import org.flywaydb.test.annotation.FlywayTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

//@FlywayTest
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@RunWith(SpringRunner::class)
//class PersistWonders {
//
//    @Autowired
//    lateinit var repo: GameStateStoreRepository
//
//    @Autowired
//    lateinit var createGameRepo: GameRepository
//
////    @Test
////    fun storeWonders() {
////        val wonderList = listOf(
////                Wonder("Via Appia", Resource(1,2,3), features = listOf(AddGold(3), RemoveGold, ExtraTurn, VictoryPoints(3))),
////                Wonder("Second wonders", Resource(3), features = listOf(DestroyBrownCard)),
////                Wonder("Third shieeet", Resource(1,2), features = listOf(DestroySilverCard)),
////                Wonder("Everybody dance", Resource(2,glass = 1), features = listOf(ExtraTurn)),
////                Wonder("Via Appia 2", Resource(1,2,3), features = listOf(AddGold(3), RemoveGold, ExtraTurn, VictoryPoints(3))),
////                Wonder("Second wonders 2", Resource(3), features = listOf(DestroyBrownCard)),
////                Wonder("Third shieeet 2", Resource(1,2), features = listOf(DestroySilverCard)),
////                Wonder("Everybody dance 2", Resource(2,glass = 1), features = listOf(ExtraTurn))
////        )
////
////        val cardBuilder = CardBuilder()
////        val cards = listOf(
////                cardBuilder.getCards().subList(0, 20),
////                cardBuilder.getCards().subList(0, 20),
////                cardBuilder.getCards().subList(0, 20))
////
////        val wonders = WondersBuilder().setupWonders(wonderList, cards)
////
////        val gameId = createGameRepo.storeInitialState(GameInitialSettings(wonderList, cards)).toString()
////
////        val id = repo.storeWonders(gameId, wonders)
////        val retrievedWonders = repo.getWonders(id)
////        assertEquals(wonders.game, retrievedWonders.game)
////        assertEquals(wonders.firstEpoh, retrievedWonders.firstEpoh)
////    }
//}