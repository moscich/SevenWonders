package com.moscichowski.wonders.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.moscichowski.wonders.builder.CardBuilder
import com.moscichowski.wonders.builder.CardJsonModule
import org.junit.Test
import kotlin.test.assertEquals

class CardBuildingTest {
    @Test
    fun buildCardsFromJson() {

        val cardBuilder = CardBuilder()
        val cards = cardBuilder.getCards()
        assertEquals(23, cards.count())
        assertEquals(Card("wycinka", CardColor.BROWN, features = listOf(ProvideResource(Resource(1)))), cards[0])
        assertEquals(Card("skład drewna", CardColor.BROWN, Resource(gold = 1), listOf(ProvideResource(Resource(1)))), cards[1])

//        fail("Just testing")
    }

    @Test
    fun justtesting() {
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(CardJsonModule())
        val card = Card("Testing", CardColor.GREEN, features = listOf(ProvideResource(Resource(1))))
        val writeValueAsString = objectMapper.writeValueAsString(card)
        println("writeValueAsString = ${writeValueAsString}")

    }


}