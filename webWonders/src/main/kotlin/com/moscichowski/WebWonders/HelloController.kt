package com.moscichowski.WebWonders

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
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
//        val query = jdbcTemplate.query("select * from actions", ActionMapper())
////        val mapper = ObjectMapper()
////        val value = mapper.readValue(query.first().action, Payload::class.java)
//
//        val payload = Gson().fromJson(query.first().action, Payload::class.java)


        val takeCard = TakeCard(Card("Test card", CardColor.BLUE, Resource(1, 2), listOf(Warehouse(WarehouseType.CLAY), GoldForWonder)))
//        val gson = GsonBuilder().registerTypeAdapter(List::class.java, ActionAdapter2()).create()
//        val json = gson.toJson(takeCard)

        return ""//json
    }

    @RequestMapping(value = ["actions"], method = [RequestMethod.POST])
    fun postAction(@RequestBody load: Action): Action {
        return load
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
        val takeCard = TakeCard(Card("Name", CardColor.RED, features = listOf(Warehouse(WarehouseType.STONE))))
        return takeCard
    }
}

class ActionSerializer : JsonSerializer<Action>() {
    override fun serialize(value: Action?, gen: JsonGenerator, serializers: SerializerProvider?) {
        gen.writeObject(Card("Name", CardColor.RED, features = listOf(Warehouse(WarehouseType.STONE), ProvideResource(Resource(1, 2, 3)))))
    }
}

class CardFeatureSerializer : JsonSerializer<CardFeature>() {
    override fun serialize(value: CardFeature?, gen: JsonGenerator, serializers: SerializerProvider?) {
        when (value) {
            is ProvideResource -> {
                gen.writeStartObject()
                gen.writeObjectField("type", "PROVIDE_RESOURCE")
                gen.writeObjectField("resource", value.resource)
                gen.writeEndObject()
            }
            is Warehouse -> {
                gen.writeStartObject()
                when(value.type){
                    WarehouseType.WOOD -> gen.writeObjectField("type", "WOOD_WAREHOUSE")
                    WarehouseType.CLAY -> gen.writeObjectField("type", "CLAY_WAREHOUSE")
                    WarehouseType.STONE -> gen.writeObjectField("type", "STONE_WAREHOUSE")

                }
                gen.writeEndObject()
            }
        }
    }
}

data class Payload(
        val test1: String,
        val howmuch: Int)