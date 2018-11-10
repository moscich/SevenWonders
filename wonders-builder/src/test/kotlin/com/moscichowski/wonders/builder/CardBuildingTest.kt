package com.moscichowski.wonders.builder

import com.moscichowski.wonders.model.*
import org.junit.Test
import kotlin.test.assertEquals

class CardBuildingTest {
    @Test
    fun `build first epoch from Json`() {
        val cardBuilder = CardBuilder()
        val cards = cardBuilder.getCards(0)
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

    @Test
    fun `build second epoch from Json`() {
        val cardBuilder = CardBuilder()
        val cards = cardBuilder.getCards(1)

        assertEquals(Card("sawmill", CardColor.BROWN, Resource(gold = 2), features = listOf(ProvideResource(Resource(2)))), cards[0])
        assertEquals(Card("brickyard", CardColor.BROWN, Resource(gold = 2), features = listOf(ProvideResource(Resource(clay = 2)))), cards[1])
        assertEquals(Card("shelf quarry", CardColor.BROWN, Resource(gold = 2), features = listOf(ProvideResource(Resource(stone = 2)))), cards[2])
        assertEquals(Card("glass blower", CardColor.SILVER, features = listOf(ProvideResource(Resource(glass = 1)))), cards[3])
        assertEquals(Card("drying room", CardColor.SILVER, features = listOf(ProvideResource(Resource(papyrus = 1)))), cards[4])
        assertEquals(Card("walls", CardColor.RED, Resource(stone = 2), features = listOf(Military(2))), cards[5])
        assertEquals(Card("forum", CardColor.YELLOW, Resource(clay = 1, gold = 3), features = listOf(ProvideSilverResource)), cards[6])
        assertEquals(Card("caravansery", CardColor.YELLOW, Resource(glass = 1, papyrus = 1, gold = 2), features = listOf(ProvideBrownResource)), cards[7])
        assertEquals(Card("customs house", CardColor.YELLOW, Resource(gold = 4), features = listOf(Customs)), cards[8])
        assertEquals(Card("tribunal", CardColor.BLUE, Resource(wood = 2, glass = 1), features = listOf(VictoryPoints(5))), cards[9])
        assertEquals(Card("horse breeders", CardColor.RED, Resource(clay = 2, wood = 1), features = listOf(Military(1)), freeSymbol = CardFreeSymbol.HORSESHOE), cards[10])
        assertEquals(Card("barracks", CardColor.RED, Resource(gold = 3), features = listOf(Military(1)), freeSymbol = CardFreeSymbol.SWORD), cards[11])
        assertEquals(Card("archery range", CardColor.RED, Resource(wood = 1, stone = 1, papyrus = 1), features = listOf(Military(2), FreeSymbol(CardFreeSymbol.TARGET))), cards[12])
        assertEquals(Card("parade ground", CardColor.RED, Resource(clay = 2, glass = 1), features = listOf(Military(2), FreeSymbol(CardFreeSymbol.HELMET))), cards[13])
        assertEquals(Card("library", CardColor.GREEN, Resource(wood = 1, stone = 1, glass = 1), features = listOf(Science(ScienceSymbol.INK), VictoryPoints(2)), freeSymbol = CardFreeSymbol.BOOK), cards[14])
        assertEquals(Card("dispensary", CardColor.GREEN, Resource(clay = 2, stone = 1), features = listOf(Science(ScienceSymbol.HERBS), VictoryPoints(2)), freeSymbol = CardFreeSymbol.COG), cards[15])
        assertEquals(Card("school", CardColor.GREEN, Resource(wood = 1, papyrus = 2), features = listOf(Science(ScienceSymbol.WHEEL), VictoryPoints(1), FreeSymbol(CardFreeSymbol.HARF))), cards[16])
        assertEquals(Card("laboratory", CardColor.GREEN, Resource(wood = 1, glass = 2), features = listOf(Science(ScienceSymbol.COMPASS), VictoryPoints(1), FreeSymbol(CardFreeSymbol.LAMP))), cards[17])
        assertEquals(Card("statue", CardColor.BLUE, Resource(clay = 2), features = listOf(VictoryPoints(4), FreeSymbol(CardFreeSymbol.COLUMN)), freeSymbol = CardFreeSymbol.MASK), cards[18])
        assertEquals(Card("temple", CardColor.BLUE, Resource(wood = 1, papyrus = 1), features = listOf(VictoryPoints(4), FreeSymbol(CardFreeSymbol.SUN)), freeSymbol = CardFreeSymbol.MOON), cards[19])
        assertEquals(Card("aqueduct", CardColor.BLUE, Resource(stone = 3), features = listOf(VictoryPoints(5)), freeSymbol = CardFreeSymbol.TEARDROP), cards[20])
        assertEquals(Card("rostrum", CardColor.BLUE, Resource(wood = 1, stone = 1), features = listOf(VictoryPoints(4), FreeSymbol(CardFreeSymbol.COURT))), cards[21])
        assertEquals(Card("brewery", CardColor.YELLOW, features = listOf(AddGold(6), FreeSymbol(CardFreeSymbol.BARREL))), cards[22])
    }
}