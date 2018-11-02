package com.moscichowski.WebWonders

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
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

        Assert.assertEquals(200, takeCard("teatr")?.statusCode?.value())
        Assert.assertEquals(200, takeCard("zielarnia")?.statusCode?.value())
        Assert.assertEquals(200, takeCard("magazyn drewna")?.statusCode?.value())
        Assert.assertEquals(200, takeCard("skryptorium")?.statusCode?.value())
        Assert.assertEquals(200, takeCard("stajnie")?.statusCode?.value())

        game = testRestTemplate.getForEntity("/games/$gameNumber", String::class.java)
        game
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