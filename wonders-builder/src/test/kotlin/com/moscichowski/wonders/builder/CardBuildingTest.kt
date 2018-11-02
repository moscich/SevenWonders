package com.moscichowski.wonders.builder

import com.moscichowski.wonders.model.*
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
        assertEquals(Card("glinianka", CardColor.BROWN, features = listOf(ProvideResource(Resource(clay = 1)))), cards[2])
        assertEquals(Card("złoża gliny", CardColor.BROWN, Resource(gold = 1), listOf(ProvideResource(Resource(clay = 1)))), cards[3])
        assertEquals(Card("kamieniołom", CardColor.BROWN, features = listOf(ProvideResource(Resource(stone = 1)))), cards[4])
        assertEquals(Card("składowisko kamienia", CardColor.BROWN, Resource(gold = 1), listOf(ProvideResource(Resource(stone = 1)))), cards[5])
        assertEquals(Card("huta szkła", CardColor.SILVER, Resource(gold = 1), listOf(ProvideResource(Resource(glass = 1)))), cards[6])
        assertEquals(Card("wytwórnia papirusu", CardColor.SILVER, Resource(gold = 1), listOf(ProvideResource(Resource(papyrus = 1)))), cards[7])
        assertEquals(Card("wieża strażnicza", CardColor.RED, features = listOf(Military(1))), cards[8])
        assertEquals(Card("warsztat", CardColor.GREEN, Resource(papyrus = 1), listOf(Science(ScienceSymbol.COMPASS), VictoryPoints(1))), cards[9])
        assertEquals(Card("apteka", CardColor.GREEN, Resource(glass = 1), listOf(Science(ScienceSymbol.WHEEL), VictoryPoints(1))), cards[10])
        assertEquals(Card("magazyn kamienia", CardColor.YELLOW, Resource(gold = 3), listOf(Warehouse(WarehouseType.STONE))), cards[11])
        assertEquals(Card("magazyn gliny", CardColor.YELLOW, Resource(gold = 3), listOf(Warehouse(WarehouseType.CLAY))), cards[12])
        assertEquals(Card("magazyn drewna", CardColor.YELLOW, Resource(gold = 3), listOf(Warehouse(WarehouseType.WOOD))), cards[13])
        assertEquals(Card("stajnie", CardColor.RED, Resource(wood = 1), listOf(Military(1), FreeSymbol(CardFreeSymbol.HORSESHOE))), cards[14])
        assertEquals(Card("garnizon", CardColor.RED, Resource(clay = 1), listOf(Military(1), FreeSymbol(CardFreeSymbol.SWORD))), cards[15])
        assertEquals(Card("palisada", CardColor.RED, Resource(gold = 2), listOf(Military(1), FreeSymbol(CardFreeSymbol.TOWER))), cards[16])
        assertEquals(Card("skryptorium", CardColor.GREEN, Resource(gold = 2), listOf(Science(ScienceSymbol.INK), FreeSymbol(CardFreeSymbol.BOOK))), cards[17])
        assertEquals(Card("zielarnia", CardColor.GREEN, Resource(gold = 2), listOf(Science(ScienceSymbol.HERBS), FreeSymbol(CardFreeSymbol.COG))), cards[18])
        assertEquals(Card("teatr", CardColor.BLUE, features = listOf(VictoryPoints(3), FreeSymbol(CardFreeSymbol.MASK))), cards[19])
        assertEquals(Card("ołtarz", CardColor.BLUE, features = listOf(VictoryPoints(3), FreeSymbol(CardFreeSymbol.MOON))), cards[20])
        assertEquals(Card("łaźnie", CardColor.BLUE, Resource(stone = 1), listOf(VictoryPoints(3), FreeSymbol(CardFreeSymbol.TEARDROP))), cards[21])
        assertEquals(Card("tawerna", CardColor.YELLOW, features = listOf(AddGold(4), FreeSymbol(CardFreeSymbol.BARREL))), cards[22])
    }
}