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
import com.moscichowski.wonders.model.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component


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
        this.addSerializer(BoardNode::class.java, BoardSerializer())
        this.addSerializer(Resource::class.java, ResourceSerializer())
        this.addSerializer(Card::class.java, CardSerializer())

        this.addDeserializer(Wonder::class.java, WonderDeserializer())
        this.addDeserializer(Action::class.java, ActionDeserializer())
        this.addDeserializer(GameInitialSettings::class.java, GameInitialSettingsDeserializer())
    }
}

data class GameInitialSettings(val wonders: List<Wonder>, val cards: List<List<Card>>)

class CardSerializer : JsonSerializer<Card>() {
    override fun serialize(value: Card, gen: JsonGenerator, serializers: SerializerProvider?) {
        gen.writeStartObject()
        gen.writeObjectField("name", value.name)
        gen.writeObjectField("color", value.color)
        if (value.cost != Resource()) {
            gen.writeObjectField("cost", value.cost)
        }
        gen.writeObjectField("features", value.features)
        if (value.freeSymbol != null) {
            gen.writeObjectField("features", value.freeSymbol)
        }

        gen.writeEndObject()
    }
}

class ResourceSerializer : JsonSerializer<Resource>() {
    override fun serialize(value: Resource, gen: JsonGenerator, serializers: SerializerProvider?) {
        gen.writeStartObject()

        if (value.wood > 0) {
            gen.writeObjectField("wood", value.wood)
        }
        if (value.clay > 0) {
            gen.writeObjectField("clay", value.clay)
        }
        if (value.stone > 0) {
            gen.writeObjectField("stone", value.stone)
        }
        if (value.glass > 0) {
            gen.writeObjectField("glass", value.glass)
        }
        if (value.papyrus > 0) {
            gen.writeObjectField("papyrus", value.papyrus)
        }
        if (value.gold > 0) {
            gen.writeObjectField("gold", value.gold)
        }

        gen.writeEndObject()
    }

}

class BoardSerializer : JsonSerializer<BoardNode>() {
    override fun serialize(value: BoardNode, gen: JsonGenerator, serializers: SerializerProvider?) {
        gen.writeStartObject()
        gen.writeObjectField("id", value.id)

        if (value.card != null) {
            gen.writeObjectField("card", value.card)
        }
        if (value.descendants.count() > 0) {
            gen.writeObjectField("descendants", value.descendants.map { it.id })
        }

        gen.writeEndObject()
    }
}

class GameInitialSettingsDeserializer : JsonDeserializer<GameInitialSettings>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): GameInitialSettings {
        val objectCodec = p.codec
        val jsonNode = objectCodec.readTree<TreeNode>(p)
        val wondersNode = jsonNode.get("wonders").traverse()
        wondersNode.codec = objectCodec
        val cardsNode = jsonNode.get("cards").traverse()
        cardsNode.codec = objectCodec
        val wonders: List<Wonder> = wondersNode.readValueAs(object : TypeReference<List<Wonder>>() {})
        val cards: List<List<Card>> = cardsNode.readValueAs(object : TypeReference<List<List<Card>>>() {})
        return GameInitialSettings(wonders, cards)
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

val actionMap = mapOf(
        Pair("CHOOSE_WONDER", Pair(ChooseWonder::class.java, "name"))
)

fun String.action(param: Any): Action? {
    val actionType = actionMap[this]!!.first
    return actionType.kotlin.primaryConstructor?.call(param)
}


data class Payload(
        val test1: String,
        val howmuch: Int)