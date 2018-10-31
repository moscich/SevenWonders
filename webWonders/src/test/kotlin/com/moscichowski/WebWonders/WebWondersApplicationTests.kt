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


}

data class ActionRequest(val type: String, val name: String)