package com.moscichowski.wonders.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.moscichowski.wonders.builder.CardJsonModule
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test

class MapperTests {
    companion object {
        val objectMapper = ObjectMapper()

        @BeforeClass
        @JvmStatic
        fun setup() {
            objectMapper.registerModule(CardJsonModule())
        }
    }
    @Test
    fun cardFeatures() {

        Assert.assertEquals(Customs, feature(json(Customs)))
        Assert.assertEquals(GoldForWonder, feature(json(GoldForWonder)))
        Assert.assertEquals(DestroyBrownCard, feature(json(DestroyBrownCard)))
        Assert.assertEquals(DestroySilverCard, feature(json(DestroySilverCard)))
        Assert.assertEquals(ExtraTurn, feature(json(ExtraTurn)))
        Assert.assertEquals(RemoveGold, feature(json(RemoveGold)))
        Assert.assertEquals(ProvideSilverResource, feature(json(ProvideSilverResource)))
        Assert.assertEquals(ProvideBrownResource, feature(json(ProvideBrownResource)))

        Assert.assertEquals(AddGold(42), feature(json(AddGold(42))))
        Assert.assertEquals(Military(24), feature(json(Military(24))))
        Assert.assertEquals(Science(ScienceSymbol.WHEEL), feature(json(Science(ScienceSymbol.WHEEL))))
        Assert.assertEquals(Warehouse(WarehouseType.WOOD), feature(json(Warehouse(WarehouseType.WOOD))))
        Assert.assertEquals(Warehouse(WarehouseType.CLAY), feature(json(Warehouse(WarehouseType.CLAY))))
        Assert.assertEquals(Warehouse(WarehouseType.STONE), feature(json(Warehouse(WarehouseType.STONE))))
        Assert.assertEquals(GoldForColor(CardColor.RED), feature(json(GoldForColor(CardColor.RED))))
        Assert.assertEquals(Guild(GuildType.WONDERS), feature(json(Guild(GuildType.WONDERS))))
        Assert.assertEquals(VictoryPoints(12), feature(json(VictoryPoints(12))))
        Assert.assertEquals(FreeSymbol(CardFreeSymbol.SWORD), feature(json(FreeSymbol(CardFreeSymbol.SWORD))))
        Assert.assertEquals(ProvideResource(Resource(1, 2, 3, 4, 5, 6)),
                feature(json(ProvideResource(Resource(1, 2, 3, 4, 5, 6)))))
    }

    private fun feature(json: String): CardFeature {
        return objectMapper.readValue(json, CardFeature::class.java)
    }

    private fun json(value: Any): String {
        return objectMapper.writeValueAsString(value)
    }
}