package com.moscichowski.WebWonders

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.http.HttpEntity




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
	fun simplePost() {
        val fileContent = WebWondersApplicationTests::class.java.getResource("/simple_game.json").readText()
        val request = HttpEntity(ActionRequest("Take Me"))
        val foo = testRestTemplate.postForObject("/actions", request, String::class.java)
        Assert.assertEquals(fileContent, foo)
        println("kaszana")
    }
}

data class ActionRequest(val name: String)