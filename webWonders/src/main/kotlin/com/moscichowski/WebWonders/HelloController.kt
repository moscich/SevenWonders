package com.moscichowski.WebWonders

import com.fasterxml.jackson.annotation.JsonView
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.*
import com.moscichowski.wonders.*
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.beans.factory.annotation.Autowired
import java.lang.reflect.Type

@RestController
@RequestMapping("/")
class HelloController {

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @RequestMapping(method = [RequestMethod.GET])
    fun index(): Any {
        val query = jdbcTemplate.query("select * from actions", ActionMapper())
//        val mapper = ObjectMapper()
//        val value = mapper.readValue(query.first().action, Payload::class.java)

        val payload = Gson().fromJson(query.first().action, Payload::class.java)


        val takeCard = TakeCard(Card("Test card", CardColor.BLUE, Resource(1, 2), listOf(Warehouse(WarehouseType.CLAY), GoldForWonder)))
        val gson = GsonBuilder().registerTypeAdapter(List::class.java, ActionAdapter2()).create()
        val json = gson.toJson(takeCard)

        return json
    }

    @RequestMapping(method = [RequestMethod.POST])
    fun postTest(@RequestBody load: Payload): String {

        jdbcTemplate.batchUpdate("insert into actions (action) values ('{\"test1\": \"Test2\", \"howmuch\": 42}')")
        return "Greetings ${load.test1} == ${load.howmuch}"
    }
}

data class Payload(
        val test1: String,
        val howmuch: Int)

class ActionAdapter2: JsonSerializer<List<CardFeature>> {
    override fun serialize(src: List<CardFeature>?, typeOfSrc: Type?, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()
//        jsonObject
        return context.serialize(listOf("afl,", "b", "c"))
    }

}

//class ActionAdapter: JsonSerializer<CardFeature> {
//    override fun serialize(src: CardFeature?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
//        val jsonObject = JsonObject()
//        when(src) {
//            is Warehouse ->
//        }
////        jsonObject.
//                "".to
//        jsonObject.addProperty("Type", "We're making something up")
//        return jsonObject
//    }
//
//    private fun Warehouse.json(): JsonObject {
//        when(type) {
//            WarehouseType.CLAY -> {
//                val jsonObject = JsonObject()
//                jsonObject.addProperty("", "")
//            }
//        }
//    }
//}

