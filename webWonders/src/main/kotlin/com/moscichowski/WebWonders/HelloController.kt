package com.moscichowski.WebWonders

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.TreeNode
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


@RestController
@RequestMapping("/")
class HelloController {

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @RequestMapping(method = [RequestMethod.GET])
    fun index(): Any {
        return "kupa"//json
    }

    @RequestMapping(value = ["actions"], method = [RequestMethod.POST])
    fun postAction(@RequestBody action: Action): Any {

//        val node = BoardNode(Card("Take Me", CardColor.BLUE))
//        val board = Board(listOf(node))
//        val game = Game(Player(6), Player(6), board)
//        val wonders = Wonders(game)
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
    override fun serialize(value: CardFeature?, gen: JsonGenerator, serializers: SerializerProvider?) {
        gen.writeStartObject()
        when (value) {
            is ProvideResource -> {
                gen.writeObjectField("type", "PROVIDE_RESOURCE")
                gen.writeObjectField("resource", value.resource)
            }
            is Warehouse -> {
                gen.writeObjectField("type", "WAREHOUSE")
                gen.writeObjectField("kind", value.type)
            }
            is AddGold -> {
                gen.writeObjectField("type", "ADD_GOLD")
                gen.writeObjectField("gold", value.gold)
            }
            is Military -> {
                gen.writeObjectField("type", "MILITARY")
                gen.writeObjectField("points", value.points)
            }
            is FreeSymbol -> {
                gen.writeObjectField("type", "FREE_SYMBOL")
                gen.writeObjectField("symbol", value.symbol)
            }
            is Science -> {
                gen.writeObjectField("type", "SCIENCE")
                gen.writeObjectField("symbol", value.science)
            }
            is GoldForColor -> {
                gen.writeObjectField("type", "GOLD_FOR_COLOR")
                gen.writeObjectField("color", value.color)
            }
            is Guild -> {
                gen.writeObjectField("type", "GUILD")
                gen.writeObjectField("kind", value.type)
            }
            is VictoryPoints -> {
                gen.writeObjectField("type", "VICTORY_POINTS")
                gen.writeObjectField("points", value.points)
            }

            is Customs -> gen.writeObjectField("type", "CUSTOMS")
            is GoldForWonder -> gen.writeObjectField("type", "GOLD_FOR_WONDER")
            is DestroyBrownCard -> gen.writeObjectField("type", "DESTROY_BROWN_CARD")
            is DestroySilverCard -> gen.writeObjectField("type", "DESTROY_SILVER_CARD")
            is ExtraTurn -> gen.writeObjectField("type", "EXTRA_TURN")
            is RemoveGold -> gen.writeObjectField("type", "REMOVE_GOLD")
            is ProvideSilverResource -> gen.writeObjectField("type", "PROVIDE_SILVER_RESOURCE")
            is ProvideBrownResource -> gen.writeObjectField("type", "PROVIDE_BROWN_RESOURCE")
        }

        gen.writeEndObject()
    }
}

data class Payload(
        val test1: String,
        val howmuch: Int)