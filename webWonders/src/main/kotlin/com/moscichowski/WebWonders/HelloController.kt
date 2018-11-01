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
        val game = Game((0 until 8).map { Wonder("Test") }, listOf())
        val wonders = Wonders(game)
//
//        wonders.takeAction(action)

        return ""
    }

    @RequestMapping(value = ["test"], method = [RequestMethod.GET])
    fun xd(): Game {

        val node = BoardNode(Card("Take Me", CardColor.BLUE))
        val board = Board(listOf(node))
        val game = Game((0 until 8).map { Wonder("Test") }, listOf())
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