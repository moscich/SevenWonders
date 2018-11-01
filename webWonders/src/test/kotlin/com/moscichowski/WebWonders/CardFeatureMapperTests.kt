package com.moscichowski.WebWonders

import com.moscichowski.wonders.model.*
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
class CardFeatureMapperTests {

    companion object {
        val objectMapper = WondersMapper()

        @BeforeClass
        @JvmStatic
        fun setup() {
        }
    }

    @Test
    fun mapCardFeatures() {
        assertEquals("{\"type\":\"CUSTOMS\"}", json(Customs))
        assertEquals("{\"type\":\"GOLD_FOR_WONDER\"}", json(GoldForWonder))
        assertEquals("{\"type\":\"DESTROY_BROWN_CARD\"}", json(DestroyBrownCard))
        assertEquals("{\"type\":\"DESTROY_SILVER_CARD\"}", json(DestroySilverCard))
        assertEquals("{\"type\":\"EXTRA_TURN\"}", json(ExtraTurn))
        assertEquals("{\"type\":\"REMOVE_GOLD\"}", json(RemoveGold))
        assertEquals("{\"type\":\"PROVIDE_SILVER_RESOURCE\"}", json(ProvideSilverResource))
        assertEquals("{\"type\":\"PROVIDE_BROWN_RESOURCE\"}", json(ProvideBrownResource))

        assertEquals("{\"type\":\"ADD_GOLD\",\"gold\":42}", json(AddGold(42)))
        assertEquals("{\"type\":\"MILITARY\",\"points\":24}", json(Military(24)))
        assertEquals("{\"type\":\"FREE_SYMBOL\",\"symbol\":\"SWORD\"}", json(FreeSymbol(CardFreeSymbol.SWORD)))
        assertEquals("{\"type\":\"SCIENCE\",\"symbol\":\"WHEEL\"}", json(Science(ScienceSymbol.WHEEL)))
        assertEquals("{\"type\":\"WAREHOUSE\",\"kind\":\"WOOD\"}", json(Warehouse(WarehouseType.WOOD)))
        assertEquals("{\"type\":\"WAREHOUSE\",\"kind\":\"CLAY\"}", json(Warehouse(WarehouseType.CLAY)))
        assertEquals("{\"type\":\"WAREHOUSE\",\"kind\":\"STONE\"}", json(Warehouse(WarehouseType.STONE)))
        assertEquals("{\"type\":\"GOLD_FOR_COLOR\",\"color\":\"RED\"}", json(GoldForColor(CardColor.RED)))
        assertEquals("{\"type\":\"GUILD\",\"kind\":\"WONDERS\"}", json(Guild(GuildType.WONDERS)))
        assertEquals("{\"type\":\"VICTORY_POINTS\",\"points\":12}", json(VictoryPoints(12)))
        assertEquals(
                "{\"type\":\"PROVIDE_RESOURCE\",\"resource\":{\"wood\":1,\"clay\":2,\"stone\":3,\"glass\":4,\"papyrus\":5,\"gold\":6}}",
                json(ProvideResource(Resource(1,2,3,4,5,6)))
        )
    }

    @Test
    fun cardFeatures() {

        assertEquals(Customs, feature(json(Customs)))
        assertEquals(GoldForWonder, feature(json(GoldForWonder)))
        assertEquals(DestroyBrownCard, feature(json(DestroyBrownCard)))
        assertEquals(DestroySilverCard, feature(json(DestroySilverCard)))
        assertEquals(ExtraTurn, feature(json(ExtraTurn)))
        assertEquals(RemoveGold, feature(json(RemoveGold)))
        assertEquals(ProvideSilverResource, feature(json(ProvideSilverResource)))
        assertEquals(ProvideBrownResource, feature(json(ProvideBrownResource)))

        assertEquals(AddGold(42), feature(json(AddGold(42))))
        assertEquals(Military(24), feature(json(Military(24))))
        assertEquals(Science(ScienceSymbol.WHEEL), feature(json(Science(ScienceSymbol.WHEEL))))
        assertEquals(Warehouse(WarehouseType.WOOD), feature(json(Warehouse(WarehouseType.WOOD))))
        assertEquals(Warehouse(WarehouseType.CLAY), feature(json(Warehouse(WarehouseType.CLAY))))
        assertEquals(Warehouse(WarehouseType.STONE), feature(json(Warehouse(WarehouseType.STONE))))
        assertEquals(GoldForColor(CardColor.RED), feature(json(GoldForColor(CardColor.RED))))
        assertEquals(Guild(GuildType.WONDERS), feature(json(Guild(GuildType.WONDERS))))
        assertEquals(VictoryPoints(12), feature(json(VictoryPoints(12))))
        assertEquals(FreeSymbol(CardFreeSymbol.SWORD), feature(json(FreeSymbol(CardFreeSymbol.SWORD))))
        assertEquals(ProvideResource(Resource(1,2,3,4,5,6)),
                feature(json(ProvideResource(Resource(1,2,3,4,5,6)))))
    }

    @Test
    fun cardFeaturesFromJson() {
        assertEquals(Customs, feature("{\"type\":\"CUSTOMS\"}"))
        assertEquals(AddGold(42), feature("{\"type\":\"ADD_GOLD\",\"gold\":42}"))
    }

    private fun feature(json: String): CardFeature {
        return objectMapper.readValue(json, CardFeature::class.java)
    }

    private fun json(value: Any): String {
        return objectMapper.writeValueAsString(value)
    }

}