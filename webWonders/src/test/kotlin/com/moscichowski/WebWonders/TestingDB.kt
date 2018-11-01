package com.moscichowski.WebWonders

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.opentable.db.postgres.embedded.EmbeddedPostgres
import com.opentable.db.postgres.embedded.FlywayPreparer
import com.opentable.db.postgres.junit.EmbeddedPostgresRules
import junit.framework.Assert.*
import org.flywaydb.test.annotation.FlywayTest
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener


@FlywayTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestExecutionListeners(DependencyInjectionTestExecutionListener::class, FlywayTestExecutionListener.class)
@TestExecutionListeners(DependencyInjectionTestExecutionListener::class )
//@TestExecutionListeners({DependencyInjectionTestExecutionListener.ja,
//    FlywayTestExecutionListener.class }
@RunWith(SpringRunner::class)
public class XdTests {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    public companion object {
        @BeforeClass
                @JvmStatic
        fun setup() {
            println("Akuku")
        }
    }

    @get:Rule
    var db = EmbeddedPostgresRules.preparedDatabase(
            FlywayPreparer.forClasspathLocation("db/migration"))



    var jdbcTemplate: JdbcTemplate? = null

    @Test
    fun hey() {
        this.jdbcTemplate = NamedParameterJdbcTemplate(db.testDatabase).jdbcTemplate
        val forClasspathLocation = FlywayPreparer.forClasspathLocation("db/migration")
        val preparedDatabase = EmbeddedPostgresRules.preparedDatabase(forClasspathLocation)

        this.jdbcTemplate?.batchUpdate("insert into actions (action) values ('{\"test1\": \"Test2\", \"howmuch\": 42}')")
        val actions = this.jdbcTemplate?.query("select * from actions") { rs, _ ->
            rs.getString(1)
        }
        println("javaClass = ${actions}")
    }

    @Autowired
    lateinit var controller: GameController

    @Test
    fun playGame() {
        controller.jdbcTemplate = NamedParameterJdbcTemplate(db.testDatabase).jdbcTemplate
        val mapper = ObjectMapper()

        val gameNumber = testRestTemplate.postForEntity("/games", null, Int::class.java).body
        for (i in 0 until 8) {
            val availableWonders = jsonNode(gameNumber, mapper)
            val name = availableWonders.first().get("name").asText()
            val response = testRestTemplate.postForEntity("/games/$gameNumber/actions", ActionRequest("CHOOSE_WONDER", name), Any::class.java)
            Assert.assertEquals(200, response.statusCode.value())
        }

        val game = testRestTemplate.getForEntity("/games/$gameNumber", String::class.java)
        val root = mapper.readTree(game.body)
        Assert.assertEquals(4, root.path("player1").path("wonders").count())
        Assert.assertEquals(4, root.path("player2").path("wonders").count())
        Assert.assertEquals(0, root.path("wonders").count())
        Assert.assertEquals("REGULAR", root.path("state").asText())
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