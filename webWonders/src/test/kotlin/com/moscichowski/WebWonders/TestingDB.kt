package com.moscichowski.WebWonders

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.moscichowski.wonders.model.Card
import com.opentable.db.postgres.embedded.EmbeddedPostgres
import junit.framework.Assert.*
import org.flywaydb.test.annotation.FlywayTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.ResponseEntity
import org.springframework.test.context.junit4.SpringRunner


@FlywayTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner::class)
class XdTests {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    var gameNumber = 0

    @Test
    fun playGame() {
        val mapper = ObjectMapper()
        mapper.registerModule(TestJsonModule())

        gameNumber = testRestTemplate.postForEntity("/games", null, Int::class.java).body ?: 0
        for (i in 0 until 8) {
            val availableWonders = jsonNode(gameNumber, mapper)
            val name = availableWonders.first().get("name").asText()
            val response = testRestTemplate.postForEntity("/games/$gameNumber/actions", ActionRequest("CHOOSE_WONDER", name), Any::class.java)
            Assert.assertEquals(200, response.statusCode.value())
        }

        var game = testRestTemplate.getForEntity("/games/$gameNumber", String::class.java)
        val root = mapper.readTree(game.body)
        Assert.assertEquals(4, root.path("player1").path("wonders").count())
        Assert.assertEquals(4, root.path("player2").path("wonders").count())
        Assert.assertEquals(0, root.path("wonders").count())
        Assert.assertEquals("REGULAR", root.path("state").asText())

        val badRequestForWarehouse = takeCard("magazyn drewna")
        Assert.assertEquals(400, badRequestForWarehouse?.statusCode?.value())
        Assert.assertEquals("{\"message\":\"Card unavailable\"}", badRequestForWarehouse?.body)

        val valuexd = mapper.readValue(game.body, SimplifiedGame::class.java)
        assertEquals(SimplifiedGame(6,0,6,0, 0), valuexd)

        takeCard200("teatr")
        takeCard200("zielarnia")
        takeCard200("magazyn drewna")
        takeCard200("skryptorium")
        takeCard200("stajnie")

        game = testRestTemplate.getForEntity("/games/$gameNumber", String::class.java)
        val value = mapper.readValue(game.body, SimplifiedGame::class.java)
        assertEquals(SimplifiedGame(2,3,2,2, 1), value)
    }

    fun takeCard200(name: String) {
        val result = testRestTemplate.postForEntity("/games/$gameNumber/actions", ActionRequest("TAKE_CARD", name), String::class.java)
        Assert.assertEquals(200, result.statusCode.value())
    }

    fun takeCard(name: String): ResponseEntity<String>? {
        return testRestTemplate.postForEntity("/games/$gameNumber/actions", ActionRequest("TAKE_CARD", name), String::class.java)
    }

    fun jsonNode(gameNumber: Int?, mapper: ObjectMapper): JsonNode {
        val game = testRestTemplate.getForEntity("/games/$gameNumber", String::class.java)
        val root = mapper.readTree(game.body)
        val availableWonders = root.path("wonders")
        return availableWonders
    }


    @Test
    @Throws(Exception::class)
    fun testEmbeddedPg() {
        try {

            EmbeddedPostgres.start().use { pg ->
                pg.postgresDatabase.connection.use { c ->
                    val s = c.createStatement()
                    val rs = s.executeQuery("SELECT 1")
                    assertTrue(rs.next())
                    assertEquals(1, rs.getInt(1))
                    assertFalse(rs.next())
                }
            }
        } catch (e: Exception) {
            print(e)
            fail()
        }

    }
}

data class SimplifiedGame(
    val player1gold: Int,
    val player1cards: Int,
    val player2gold: Int,
    val player2cards: Int,
    val currentPlayer: Int
)

class TestJsonModule : SimpleModule() {
    init {
        this.addDeserializer(SimplifiedGame::class.java, SimplifiedGameDeserializer())
    }
}

class SimplifiedGameDeserializer: JsonDeserializer<SimplifiedGame>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): SimplifiedGame {
        val objectCodec = p.codec
        val jsonNode = objectCodec.readTree<TreeNode>(p)
        val player1Parser = jsonNode.get("player1").traverse()
        val player2Parser = jsonNode.get("player2").traverse()
        val currentPlayer = jsonNode.get("currentPlayer").traverse().intValue
        player1Parser.codec = objectCodec
        player2Parser.codec = objectCodec
        val player1Node = objectCodec.readTree<TreeNode>(player1Parser)
        val player2Node = objectCodec.readTree<TreeNode>(player2Parser)
        val player1gold = player1Node.get("gold").traverse().intValue
        val player2gold = player2Node.get("gold").traverse().intValue
        val cards1Parser = player1Node.get("cards").traverse()
        val cards2Parser = player2Node.get("cards").traverse()
        cards1Parser.codec = objectCodec
        cards2Parser.codec = objectCodec
        val player1cards: List<Any> = cards1Parser.readValueAs(object : TypeReference<List<Any>>() {})
        val player2cards: List<Any> = cards2Parser.readValueAs(object : TypeReference<List<Any>>() {})

        return SimplifiedGame(player1gold, player1cards.count(), player2gold, player2cards.count(), currentPlayer)
    }

}