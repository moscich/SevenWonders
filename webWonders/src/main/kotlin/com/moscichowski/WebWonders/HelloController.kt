package com.moscichowski.WebWonders

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.TextNode
import com.moscichowski.wonders.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.lang.Error
import kotlin.reflect.full.primaryConstructor


@RestController
@RequestMapping("/")
class HelloController {

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @RequestMapping(method = [RequestMethod.GET])
    fun index(): Any {

        throw Error("Akuku")

        return "kupa"//json
    }

    @RequestMapping(value = ["actions"], method = [RequestMethod.POST])
    fun postAction(@RequestBody action: Action): Any {

        val node = BoardNode(Card("Take Me", CardColor.BLUE))
        val board = Board(listOf(node))
        val game = Game(board, (0 until 8).map { Wonder("Test") })
        val wonders = Wonders(game)
//
//        wonders.takeAction(action)

        return ""
    }

    @RequestMapping(value = ["test"], method = [RequestMethod.GET])
    fun xd(): Game {

        val node = BoardNode(Card("Take Me", CardColor.BLUE))
        val board = Board(listOf(node))
        val game = Game(board, (0 until 8).map { Wonder("Test") })
        val wonders = Wonders(game)
//
//        wonders.takeAction(action)

        return game
    }

    @RequestMapping(method = [RequestMethod.POST])
    fun postTest(@RequestBody load: Payload): String {

        jdbcTemplate.batchUpdate("insert into actions (action) values ('{\"test1\": \"Test2\", \"howmuch\": 42}')")

        return "Greetings ${load.test1} == ${load.howmuch}"
    }
}

@Service
class ActionJsonModule : SimpleModule() {
    init {
        this.addSerializer(Action::class.java, ActionSerializer())
        this.addSerializer(CardFeature::class.java, CardFeatureSerializer())

        this.addDeserializer(Wonder::class.java, WonderDeserializer())
        this.addDeserializer(Action::class.java, ActionDeserializer())
        this.addDeserializer(CardFeature::class.java, CardFeatureDeserializer())
    }
}

class WonderDeserializer : JsonDeserializer<Wonder>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Wonder {
        val objectCodec = p.codec
        val jsonNode = objectCodec.readTree<TreeNode>(p)
        val costNode = jsonNode.get("cost").traverse()
        val featuresNode = jsonNode.get("features").traverse()
        featuresNode.codec = objectCodec
        val features: List<CardFeature> = featuresNode.readValueAs(object : TypeReference<List<CardFeature>>() {})
        costNode.codec = objectCodec
        val cost = costNode.readValueAs(Resource::class.java)
        return Wonder((jsonNode.get("name") as TextNode).asText(), cost, features)
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

class ActionDeserializer : JsonDeserializer<Action>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Action {
        val objectCodec = p.codec
        val jsonNode = objectCodec.readTree<TreeNode>(p)
        val typeString = (jsonNode.get("type") as TextNode).asText()
        val paramName = actionMap[typeString]?.second
        val paramType = actionMap[typeString]?.first
        val paramNode = jsonNode.get(paramName).traverse()
        paramNode.codec = objectCodec
        val paramData = paramNode.readValueAs(paramType)

        return paramData
    }
}

class ActionSerializer : JsonSerializer<Action>() {
    override fun serialize(value: Action, gen: JsonGenerator, serializers: SerializerProvider?) {
        gen.writeStartObject()
        when (value) {
            is ChooseWonder -> {
                gen.writeObjectField("type", "CHOOSE_WONDER")
                gen.writeObjectField("name", value.wonderName)
            }
        }

        gen.writeEndObject()
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

val actionMap = mapOf(
        Pair("CHOOSE_WONDER", Pair(ChooseWonder::class.java, "name"))
)

fun CardFeature.type(): String {
    return featureMap[this::class.java]!!.first
}

fun String.cardFeature(param: Any? = null): CardFeature? {
    val featureType = featureMapString[this]!!.first
    return featureType.kotlin.objectInstance ?: featureType.kotlin.primaryConstructor?.call(param)
}

fun String.action(param: Any): Action? {
    val actionType = actionMap[this]!!.first
    return actionType.kotlin.primaryConstructor?.call(param)
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

data class Payload(
        val test1: String,
        val howmuch: Int)