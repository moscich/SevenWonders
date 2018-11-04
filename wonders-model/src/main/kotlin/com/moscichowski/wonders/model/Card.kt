package com.moscichowski.wonders.model

data class Card(val name: String,
                val color: CardColor,
                val cost: Resource = Resource(),
                val features: List<CardFeature> = listOf(),
                val freeSymbol: CardFreeSymbol? = null)

enum class CardColor {
    BROWN, SILVER, YELLOW, BLUE,

    RED,

    GREEN
}

data class Resource(val wood: Int = 0,
                    val clay: Int = 0,
                    val stone: Int = 0,
                    val glass: Int = 0,
                    val papyrus: Int = 0,
                    val gold: Int = 0) {
    operator fun plus(sum: Resource): Resource {
        return Resource(wood = sum.wood + wood,
                clay = sum.clay + clay,
                stone = sum.stone + stone,
                glass = sum.glass + glass,
                papyrus = sum.papyrus + papyrus)
    }

    fun combine(resource: Resource): List<Resource> {
        val result = mutableListOf<Resource>()
        resource.decompose().filter { it != Resource() }.forEach {
            result.add(this + it)
        }
        return result
    }

    private fun decompose(): List<Resource> {
        return listOf(
                Resource(wood = wood),
                Resource(clay = clay),
                Resource(stone = stone),
                Resource(papyrus = papyrus),
                Resource(glass = glass),
                Resource(gold = gold))
    }
}

enum class CardFreeSymbol {
    SWORD, HORSESHOE, TOWER, BOOK,

    COG,

    MASK,

    MOON,

    TEARDROP,

    BARREL
}

enum class ScienceSymbol {
    WHEEL, COMPASS, INK,

    HERBS
}

enum class ScienceToken {
    ENGINEERING, ARCHITECTURE, CONSTRUCTION, ECONOMY, STRATEGY, THEOLOGY, CITY_PLANNING, AGRICULTURE, MATHEMATICS, PHILOSOPHY
}

enum class WarehouseType {
    WOOD {
        override fun cost(resource: Resource): Int {
            return resource.wood
        }
    },
    CLAY {
        override fun cost(resource: Resource): Int {
            return resource.clay
        }
    },
    STONE {
        override fun cost(resource: Resource): Int {
            return resource.stone
        }
    };

    abstract fun cost(resource: Resource): Int
}

sealed class CardFeature
data class ProvideResource(val resource: Resource) : CardFeature()
data class AddGold(val gold: Int) : CardFeature()
data class Military(val points: Int) : CardFeature()
data class FreeSymbol(val symbol: CardFreeSymbol) : CardFeature()
data class Science(val science: ScienceSymbol) : CardFeature()
data class Warehouse(val type: WarehouseType) : CardFeature()
data class GoldForColor(val color: CardColor) : CardFeature()
data class Guild(val type: GuildType) : CardFeature()
data class VictoryPoints(val points: Int) : CardFeature()
object GoldForWonder : CardFeature()
object Customs : CardFeature()
object DestroyBrownCard : CardFeature()
object DestroySilverCard : CardFeature()
object ExtraTurn : CardFeature()
object RemoveGold : CardFeature()
object ProvideSilverResource : CardFeature()
object ProvideBrownResource : CardFeature()

enum class GuildType {
    YELLOW, BROWN_SILVER, WONDERS, BLUE, GREEN, GOLD, RED
}

data class Wonder(var name: String, var cost: Resource = Resource(), var features: List<CardFeature> = mutableListOf())