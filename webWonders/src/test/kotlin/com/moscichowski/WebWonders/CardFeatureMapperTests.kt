package com.moscichowski.WebWonders

import com.fasterxml.jackson.databind.ObjectMapper
import com.moscichowski.wonders.*
import org.junit.Assert.*
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
class CardFeatureMapperTests {

    companion object {
        val objectMapper = ObjectMapper()

        @BeforeClass
        @JvmStatic
        fun setup() {
            objectMapper.registerModule(ActionJsonModule())
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

    private fun json(value: Any): String {
        return objectMapper.writeValueAsString(value)
    }

}