package com.moscichowski.WebWonders

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.moscichowski.WebWonders.common.GameCreated
import com.moscichowski.WebWonders.security.AuthUser
import com.moscichowski.WebWonders.security.FacebookAuthUserWrapper
import junit.framework.Assert.*
import org.flywaydb.test.annotation.FlywayTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.*
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.client.RestTemplate
import kotlin.test.*

@FlywayTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner::class)
class XdTests {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    var gameNumber = 0

    private val objectMapper: ObjectMapper
        get() {
            val mapper = ObjectMapper()
            mapper.registerModule(TestJsonModule())
            return mapper
        }

//    @Test
//    fun playGame() {
//        val mapper = objectMapper
//
//        gameNumber = createGame()
//        for (i in 0 until 8) {
//            val name = availableWonderName()
//            takeWonder200(name)
//        }
//
//        val game = testRestTemplate.getForEntity("/games/$gameNumber", String::class.java)
//        val root = mapper.readTree(game.body)
//        Assert.assertEquals(4, root.path("player1").path("wonders").count())
//        Assert.assertEquals(4, root.path("player2").path("wonders").count())
//        Assert.assertEquals(0, root.path("wonders").count())
//        Assert.assertEquals("REGULAR", root.path("state").asText())
//
//        val badRequestForWarehouse = takeCard("magazyn gliny")
//        Assert.assertEquals(400, badRequestForWarehouse?.statusCode?.value())
//        Assert.assertEquals("{\"message\":\"Card unavailable\"}", badRequestForWarehouse?.body)
//
//        assertGame(6,0,6,0,0)
//        takeCard200("wytwórnia papirusu")
//        takeCard200("huta szkła")
//        takeCard200("magazyn gliny")
//        takeCard200("magazyn kamienia")
//        assertGame(2,2,2,2,0)
//        takeCard200("warsztat")
//        takeCard200("apteka")
//        takeCard200("stajnie")
//        takeCard200("wieża strażnicza")
//        assertGame(0,4,2,4,0)
//        sellCard200("magazyn drewna")
//        sellCard200("palisada")
//        takeCard200("garnizon")
//        sellCard200("składowisko kamienia")
//        sellCard200("kamieniołom")
//        buildWonder200("Pireus", "złoża gliny")
////        assertGame(9,4,1,4,1)
//
//        sellCard200("glinianka")
//        sellCard200("skryptorium")
//        takeCard200("zielarnia")
//        takeCard200("teatr")
//        takeCard200("skład drewna")
//        val sellCard200 = takeCard200("wycinka")
//        // AGE 2 begins
//        takeCard400("horse breeders")
//        selectPlayer200(1)
//        sellCard200("barracks")
//    }

    fun assertGame(player1gold: Int,
                   player1cards: Int,
                   player2gold: Int,
                   player2cards: Int,
                   currentPlayer: Int) {
        val game = testRestTemplate.getForEntity("/games/$gameNumber", String::class.java)
        val value = objectMapper.readValue(game.body, SimplifiedGame::class.java)
        assertEquals(SimplifiedGame(player1gold, player1cards, player2gold, player2cards, currentPlayer), value)
    }

    fun createGame(): Int {
        return testRestTemplate.postForEntity("/games", null, Int::class.java).body ?: 0
    }

    fun takeWonder200(name: String) {
        val response = testRestTemplate.postForEntity("/games/$gameNumber/actions", ActionRequest("CHOOSE_WONDER", name), String::class.java)
        Assert.assertEquals(200, response.statusCode.value())
    }

    fun takeCard200(name: String): String? {
        val result = testRestTemplate.postForEntity("/games/$gameNumber/actions", ActionRequest("TAKE_CARD", name), String::class.java)
        Assert.assertEquals(200, result.statusCode.value())
        return result.body
    }

    fun takeCard400(name: String): String? {
        val result = testRestTemplate.postForEntity("/games/$gameNumber/actions", ActionRequest("TAKE_CARD", name), String::class.java)
        Assert.assertEquals(400, result.statusCode.value())
        return result.body
    }

    fun buildWonder200(name: String, card: String) {
        val result = testRestTemplate.postForEntity("/games/$gameNumber/actions", WonderBuildRequest(name, card), String::class.java)
        Assert.assertEquals(200, result.statusCode.value())
    }

    fun sellCard200(name: String): String? {
        val result = testRestTemplate.postForEntity("/games/$gameNumber/actions", ActionRequest("SELL_CARD", name), String::class.java)
        Assert.assertEquals(200, result.statusCode.value())
        return result.body
    }

    fun takeCard(name: String): ResponseEntity<String>? {
        return testRestTemplate.postForEntity("/games/$gameNumber/actions", ActionRequest("TAKE_CARD", name), String::class.java)
    }

    fun selectPlayer200(playerNo: Int): ResponseEntity<String>? {
        return testRestTemplate.postForEntity("/games/$gameNumber/actions", ChoosePlayerRequest("CHOOSE_PLAYER", playerNo), String::class.java)
    }

    fun availableWonderName(): String {
        val game = testRestTemplate.getForEntity("/games/$gameNumber", String::class.java)
        val root = objectMapper.readTree(game.body)
        val availableWonders = root.path("wonders")
        return availableWonders.first().get("name").asText()
    }

    @MockBean
    lateinit var restTemplate: RestTemplate

    @Test
    fun `authentication failed game creation`() {
        Mockito.`when`(restTemplate.getForEntity("https://graph.facebook.com/debug_token?input_token=Something-noth-valid&access_token=1559710037462455|F_CkzUCoMC_tKa0uy5JqZX1ECu8", FacebookAuthUserWrapper::class.java))
                .thenReturn(ResponseEntity(FacebookAuthUserWrapper(AuthUser("", false)), HttpStatus.ACCEPTED))
        val headers = HttpHeaders()
        headers.add("Authorization", "Bearer Something-noth-valid")
        val httpEntity = HttpEntity("parameters", headers)
        assertFails { testRestTemplate.exchange("/games", HttpMethod.POST, httpEntity, Any::class.java) }
    }

    private fun <T> anyObject(): T {
        Mockito.anyObject<T>()
        return uninitialized()
    }

    private fun <T> uninitialized(): T = null as T
    @Test
    fun `face authenticated game creation`() {
        Mockito.`when`(restTemplate.getForEntity("https://graph.facebook.com/debug_token?input_token=XD-Token&access_token=1559710037462455|F_CkzUCoMC_tKa0uy5JqZX1ECu8", FacebookAuthUserWrapper::class.java))
                .thenReturn(ResponseEntity(FacebookAuthUserWrapper(AuthUser("xyz", true)), HttpStatus.ACCEPTED))

        val headers = HttpHeaders()
        headers.add("Authorization", "Bearer XD-Token")
        val httpEntity = HttpEntity("parameters", headers)

        val gameCreated = testRestTemplate.exchange<Map<String, String>>("/games", HttpMethod.POST, httpEntity, object : TypeReference<Map<String, String>>() {}).body
        assertNotNull(gameCreated)
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