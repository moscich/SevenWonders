package com.moscichowski.wonders.builder

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.TextNode
import com.moscichowski.wonders.model.*
import kotlin.reflect.full.primaryConstructor

open class CardJsonModule : SimpleModule() {
    init {
        this.addSerializer(CardFeature::class.java, CardFeatureSerializer())
        this.addDeserializer(CardFeature::class.java, CardFeatureDeserializer())

        this.addDeserializer(Card::class.java, CardDeserializer())
    }
}

class CardDeserializer : JsonDeserializer<Card>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Card {
        val objectCodec = p.codec
        val jsonNode = objectCodec.readTree<TreeNode>(p)
        val cost = cost(jsonNode, objectCodec)
        val features: List<CardFeature> = features(jsonNode, objectCodec)
        val color = TEMPORARYSOLUTION(jsonNode, objectCodec)

        return Card((jsonNode.get("name") as TextNode).asText(), color, cost, features)
    }

    fun TEMPORARYSOLUTION(jsonNode: TreeNode, objectCodec: ObjectCodec?): CardColor {
        val get = jsonNode.get("color") ?: return CardColor.RED
        val colorNode = get.traverse()
        colorNode.codec = objectCodec
        val color = colorNode.readValueAs(CardColor::class.java)
        return color
    }

    fun features(jsonNode: TreeNode, objectCodec: ObjectCodec?): List<CardFeature> {
        val featureJson = jsonNode.get("features") ?: return listOf()
        val featuresNode = featureJson.traverse()
        featuresNode.codec = objectCodec
        return featuresNode.readValueAs(object : TypeReference<List<CardFeature>>() {})
    }

    fun cost(jsonNode: TreeNode, objectCodec: ObjectCodec?): Resource {
        val costJson = jsonNode.get("cost") ?: return Resource()
        val costNode = costJson.traverse()
        costNode.codec = objectCodec
        return costNode.readValueAs<Resource>(Resource::class.java)
    }
}

class CardFeatureDeserializer : JsonDeserializer<CardFeature>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): CardFeature {
        val objectCodec = p.codec
        val jsonNode = objectCodec.readTree<TreeNode>(p)
        val featureString = (jsonNode.get("type") as TextNode).asText()
        val paramName = featureMapString[featureString]?.second
        if (paramName != null) {
            val paramType = featureMapString[featureString]!!.third
            val paramNode = jsonNode.get(paramName).traverse()
            paramNode.codec = objectCodec
            val paramData = paramNode.readValueAs(paramType)
            return featureString.cardFeature(paramData)!!
        }
        return featureString.cardFeature()!!
    }
}

class CardFeatureSerializer : JsonSerializer<CardFeature>() {
    override fun serialize(value: CardFeature, gen: JsonGenerator, serializers: SerializerProvider?) {
        gen.writeStartObject()
        gen.writeObjectField("type", value.type())
        val extras = value.extras()
        if (extras != null) {
            gen.writeObjectField(extras.first, extras.second)
        }

        gen.writeEndObject()
    }
}

fun CardFeature.extras(): Pair<String, Any>? {
    return when (this) {
        is ProvideResource -> Pair("resource", resource)
        is Warehouse -> Pair("kind", type)
        is AddGold -> Pair("gold", gold)
        is Military -> Pair("points", points)
        is FreeSymbol -> Pair("symbol", symbol)
        is Science -> Pair("symbol", science)
        is GoldForColor -> Pair("color", color)
        is Guild -> Pair("kind", type)
        is VictoryPoints -> Pair("points", points)
        else -> {
            null
        }
    }
}

fun CardFeature.type(): String {
    return featureMap[this::class.java]!!.first
}

fun String.cardFeature(param: Any? = null): CardFeature? {
    val featureType = featureMapString[this]!!.first
    return featureType.kotlin.objectInstance ?: featureType.kotlin.primaryConstructor?.call(param)
}

val featureMap = mapOf(
        Pair(ProvideResource::class.java, Triple("PROVIDE_RESOURCE", "resource", Resource::class.java)),
        Pair(Warehouse::class.java, Triple("WAREHOUSE", "kind", WarehouseType::class.java)),
        Pair(AddGold::class.java, Triple("ADD_GOLD", "gold", Int::class.java)),
        Pair(Military::class.java, Triple("MILITARY", "points", Int::class.java)),
        Pair(FreeSymbol::class.java, Triple("FREE_SYMBOL", "symbol", CardFreeSymbol::class.java)),
        Pair(Science::class.java, Triple("SCIENCE", "symbol", ScienceSymbol::class.java)),
        Pair(GoldForColor::class.java, Triple("GOLD_FOR_COLOR", "color", CardColor::class.java)),
        Pair(Guild::class.java, Triple("GUILD", "kind", GuildType::class.java)),
        Pair(VictoryPoints::class.java, Triple("VICTORY_POINTS", "points", Int::class.java)),
        Pair(Customs::class.java, Triple("CUSTOMS", null, null)),
        Pair(GoldForWonder::class.java, Triple("GOLD_FOR_WONDER", null, null)),
        Pair(DestroyBrownCard::class.java, Triple("DESTROY_BROWN_CARD", null, null)),
        Pair(DestroySilverCard::class.java, Triple("DESTROY_SILVER_CARD", null, null)),
        Pair(ExtraTurn::class.java, Triple("EXTRA_TURN", null, null)),
        Pair(RemoveGold::class.java, Triple("REMOVE_GOLD", null, null)),
        Pair(ProvideSilverResource::class.java, Triple("PROVIDE_SILVER_RESOURCE", null, null)),
        Pair(ProvideBrownResource::class.java, Triple("PROVIDE_BROWN_RESOURCE", null, null))
)

val featureMapString = mapOf(
        Pair("PROVIDE_RESOURCE", Triple(ProvideResource::class.java, "resource", Resource::class.java)),
        Pair("WAREHOUSE", Triple(Warehouse::class.java, "kind", WarehouseType::class.java)),
        Pair("ADD_GOLD", Triple(AddGold::class.java, "gold", Int::class.java)),
        Pair("MILITARY", Triple(Military::class.java, "points", Int::class.java)),
        Pair("FREE_SYMBOL", Triple(FreeSymbol::class.java, "symbol", CardFreeSymbol::class.java)),
        Pair("SCIENCE", Triple(Science::class.java, "symbol", ScienceSymbol::class.java)),
        Pair("GOLD_FOR_COLOR", Triple(GoldForColor::class.java, "color", CardColor::class.java)),
        Pair("GUILD", Triple(Guild::class.java, "kind", GuildType::class.java)),
        Pair("VICTORY_POINTS", Triple(VictoryPoints::class.java, "points", Int::class.java)),
        Pair("CUSTOMS", Triple(Customs::class.java, null, null)),
        Pair("GOLD_FOR_WONDER", Triple(GoldForWonder::class.java, null, null)),
        Pair("DESTROY_BROWN_CARD", Triple(DestroyBrownCard::class.java, null, null)),
        Pair("DESTROY_SILVER_CARD", Triple(DestroySilverCard::class.java, null, null)),
        Pair("EXTRA_TURN", Triple(ExtraTurn::class.java, null, null)),
        Pair("REMOVE_GOLD", Triple(RemoveGold::class.java, null, null)),
        Pair("PROVIDE_SILVER_RESOURCE", Triple(ProvideSilverResource::class.java, null, null)),
        Pair("PROVIDE_BROWN_RESOURCE", Triple(ProvideBrownResource::class.java, null, null))
)