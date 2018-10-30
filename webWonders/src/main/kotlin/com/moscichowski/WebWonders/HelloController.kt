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
import com.fasterxml.jackson.databind.node.IntNode
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

    @RequestMapping(method = [RequestMethod.POST])
    fun postTest(@RequestBody load: Payload): String {

        jdbcTemplate.batchUpdate("insert into actions (action) values ('{\"test1\": \"Test2\", \"howmuch\": 42}')")
        return "Greetings ${load.test1} == ${load.howmuch}"
    }
}

@Service
class ActionJsonModule internal constructor() : SimpleModule() {
    init {
        this.addDeserializer(Action::class.java, ActionDeserializer())
        this.addSerializer(Action::class.java, ActionSerializer())
        this.addSerializer(CardFeature::class.java, CardFeatureSerializer())
        this.addDeserializer(Wonder::class.java, WonderDeserializer())
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
        val asText = (jsonNode.get("type") as TextNode).asText()
        val first = featureMap.filter { it.value == asText }.keys.first()
        val param = featureParamMap[first]
        if (param != null) {
            val paramNode = jsonNode.get(param).traverse()
            paramNode.codec = objectCodec
            val listOf = listOf(Resource::class.java, Int::class.java, GuildType::class.java, CardColor::class.java)
            for (type in listOf) {
                try {
                    val paramData = paramNode.readValueAs(type)
                    return asText.cardFeature(paramData)!!

                } catch (e: Exception) {
                    println("javaClass = ${e}")
                }
            }
//            val paramData: Int = paramNode.readValueAs(object : TypeReference<Int>() {})
        }
        return asText.cardFeature()!!
    }
}

class ActionDeserializer : JsonDeserializer<Action>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Action {
        val objectCodec = p.codec
        val jsonNode = objectCodec.readTree<TreeNode>(p)
        val actionType = jsonNode.get("action")
        val name = (jsonNode.get("name") as TextNode).asText()
//        val takeCard = TakeCard(Card("Name", CardColor.RED, features = listOf(Warehouse(WarehouseType.STONE))))
        return TakeCard(name)
    }
}

class ActionSerializer : JsonSerializer<Action>() {
    override fun serialize(value: Action?, gen: JsonGenerator, serializers: SerializerProvider?) {
        gen.writeObject(Card("Name", CardColor.RED, features = listOf(Warehouse(WarehouseType.STONE), ProvideResource(Resource(1, 2, 3)))))
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
        Pair(ProvideResource::class.java, "PROVIDE_RESOURCE"),
        Pair(ProvideResource::class.java, "PROVIDE_RESOURCE"),
        Pair(Warehouse::class.java, "WAREHOUSE"),
        Pair(AddGold::class.java, "ADD_GOLD"),
        Pair(Military::class.java, "MILITARY"),
        Pair(FreeSymbol::class.java, "FREE_SYMBOL"),
        Pair(Science::class.java, "SCIENCE"),
        Pair(GoldForColor::class.java, "GOLD_FOR_COLOR"),
        Pair(Guild::class.java, "GUILD"),
        Pair(VictoryPoints::class.java, "VICTORY_POINTS"),
        Pair(Customs::class.java, "CUSTOMS"),
        Pair(GoldForWonder::class.java, "GOLD_FOR_WONDER"),
        Pair(DestroyBrownCard::class.java, "DESTROY_BROWN_CARD"),
        Pair(DestroySilverCard::class.java, "DESTROY_SILVER_CARD"),
        Pair(ExtraTurn::class.java, "EXTRA_TURN"),
        Pair(RemoveGold::class.java, "REMOVE_GOLD"),
        Pair(ProvideSilverResource::class.java, "PROVIDE_SILVER_RESOURCE"),
        Pair(ProvideBrownResource::class.java, "PROVIDE_BROWN_RESOURCE")
)

val featureParamMap = mapOf(
        Pair(ProvideResource::class.java, "resource"),
        Pair(Warehouse::class.java, "kind"),
        Pair(AddGold::class.java, "gold"),
        Pair(Military::class.java, "points"),
        Pair(FreeSymbol::class.java, "symbol"),
        Pair(Science::class.java, "symbol"),
        Pair(GoldForColor::class.java, "color"),
        Pair(Guild::class.java, "kind"),
        Pair(VictoryPoints::class.java, "points")
)

fun CardFeature.type(): String {
    return featureMap[this::class.java]!!
}

fun String.cardFeature(xD: Any? = null): CardFeature? {
    val first = featureMap.filter { it.value == this }.keys.first()
    println(first.kotlin)
    val objectInstance = first.kotlin.objectInstance
    if (objectInstance != null)
        return first.kotlin.objectInstance
    else {
        return first.kotlin.primaryConstructor?.call(xD)
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

data class Payload(
        val test1: String,
        val howmuch: Int)