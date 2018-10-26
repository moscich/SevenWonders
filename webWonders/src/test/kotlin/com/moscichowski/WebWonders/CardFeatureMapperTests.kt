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
    }

    private fun json(value: Any): String {
        return objectMapper.writeValueAsString(value)
    }

}








//{
//    "type": "CUSTOMS"
//}