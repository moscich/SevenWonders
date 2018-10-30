package com.moscichowski.WebWonders

import com.fasterxml.jackson.core.type.TypeReference
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

    val featureMap = mapOf(Pair(ProvideResource::class.java, "PROVIDE_RESOURCE"))

    @Test
    fun cardFeatures() {
        val provide = ProvideResource(Resource())
        assertEquals("PROVIDE_RESOURCE", featureMap[provide::class.java])
        assertEquals(GoldForWonder, feature(json(GoldForWonder)))
    }

    @Test
    fun cardFeaturesFromJson() {
        assertEquals(Customs, feature("{\"type\":\"CUSTOMS\"}"))
        assertEquals(AddGold(42), feature("{\"type\":\"ADD_GOLD\",\"gold\":42}"))
    }

//    @Test
//    fun parseWonders() {
//        val wonder = Wonder("Via Appia", Resource(1, 2, 3), features = listOf(AddGold(3), RemoveGold, ExtraTurn, VictoryPoints(3)))
//        val wonderList = listOf(
////                Wonder("Test")
//                wonder
////                Wonder("Second wonder", Resource(3), features = listOf(DestroyBrownCard)),
////                Wonder("Third shieeet", Resource(1,2), features = listOf(DestroySilverCard)),
////                Wonder("Everybody dance", Resource(2,glass = 1), features = listOf(ExtraTurn))
//        )
//
//        val writeValueAsString = objectMapper.writeValueAsString(wonderList)
//        val readValue: List<Wonder> = objectMapper.readValue(writeValueAsString, object: TypeReference<List<Wonder>>() {})
//        assertEquals(wonder, readValue.first())
//    }

    private fun feature(json: String): CardFeature {
        return objectMapper.readValue(json, CardFeature::class.java)
    }

    private fun json(value: Any): String {
        return objectMapper.writeValueAsString(value)
    }

}