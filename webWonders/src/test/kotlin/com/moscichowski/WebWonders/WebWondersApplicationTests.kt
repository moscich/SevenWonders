package com.moscichowski.WebWonders

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.junit4.SpringRunner


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
data class ChoosePlayerRequest(val type: String, val playerNo: Int)
data class WonderBuildRequest(val name: String, val card: String) {
    val type = "BUILD_WONDER"
}