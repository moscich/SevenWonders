package com.moscichowski.WebWonders

import com.fasterxml.jackson.databind.JsonNode
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import com.fasterxml.jackson.databind.ObjectMapper



@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebWondersApplicationTests {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

	@Test
	fun contextLoads() {
        val result = testRestTemplate.getForEntity("/", String::class.java)
        println(result)
    }

    @Test
    fun playGame() {
        val mapper = ObjectMapper()

        val gameNumber = testRestTemplate.postForEntity("/games", null, Int::class.java).body
        for (i in 0 until 8) {
            val availableWonders = jsonNode(gameNumber, mapper)
            val name = availableWonders.first().get("name").asText()
            val response = testRestTemplate.postForEntity("/games/$gameNumber/actions", ActionRequest("CHOOSE_WONDER", name), Any::class.java)
            assertEquals(200, response.statusCode.value())
        }

        val game = testRestTemplate.getForEntity("/games/$gameNumber", String::class.java)
        val root = mapper.readTree(game.body)
        assertEquals(4, root.path("player1").path("wonders").count())
        assertEquals(4, root.path("player2").path("wonders").count())
        assertEquals(0, root.path("wonders").count())
        assertEquals("REGULAR", root.path("state").asText())
    }

    fun jsonNode(gameNumber: Int?, mapper: ObjectMapper): JsonNode {
        val game = testRestTemplate.getForEntity("/games/$gameNumber", String::class.java)
        val root = mapper.readTree(game.body)
        val availableWonders = root.path("wonders")
        return availableWonders
    }
}

data class ActionRequest(val type: String, val name: String)