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

        assertEquals(
                "{\"type\":\"PROVIDE_RESOURCE\",\"resource\":{\"wood\":1,\"clay\":2,\"stone\":3,\"glass\":4,\"papyrus\":5,\"gold\":6}}",
                json(ProvideResource(Resource(1,2,3,4,5,6)))
        )
        assertEquals("{\"type\":\"ADD_GOLD\",\"gold\":42}", json(AddGold(42)))
        assertEquals("{\"type\":\"MILITARY\",\"points\":24}", json(Military(24)))
        assertEquals("{\"type\":\"FREE_SYMBOL\",\"symbol\":\"SWORD\"}", json(FreeSymbol(CardFreeSymbol.SWORD)))

//        FreeSymbol(val symbol: CardFreeSymbol) : CardFeature()
//        Science(val science: ScienceSymbol) : CardFeature()
//        Warehouse(val type: WarehouseType) : CardFeature()
//        GoldForColor(val color: CardColor) : CardFeature()
//        Guild(val type: GuildType) : CardFeature()
//        VictoryPoints(val points: Int) : CardFeature()
    }

    private fun json(value: Any): String {
        return objectMapper.writeValueAsString(value)
    }

}








//{
//    "type": "CUSTOMS"
//}