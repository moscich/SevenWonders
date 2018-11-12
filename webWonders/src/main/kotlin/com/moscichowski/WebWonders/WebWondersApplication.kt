package com.moscichowski.WebWonders

import com.fasterxml.jackson.databind.ObjectMapper
import com.moscichowski.WebWonders.security.AuthUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@SpringBootApplication
class WebWondersApplication

fun main(args: Array<String>) {
    runApplication<WebWondersApplication>(*args)
}

@Component
class MyRunner: CommandLineRunner {
    val restTemplate: RestTemplate = RestTemplateBuilder().rootUri("http://localhost:8080").build()

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private var gameNumber = 0

    override fun run(vararg args: String?) {
//        val forEntity = restTemplate.getForEntity("https://graph.facebook.com/1701400063299376?access_token=1559710037462455|F_CkzUCoMC_tKa0uy5JqZX1ECu8", AuthUser::class.java)
//        println("forEntity = ${forEntity}")
//        gameNumber = createGame()
//        println(gameNumber)
//        for (i in 0 until 8) {
//            val name = availableWonderName()
//            takeWonder(name)
//        }
    }

    fun createGame(): Int {
        return restTemplate.postForEntity("/games", null, Int::class.java).body ?: 0
    }

    fun takeWonder(name: String) {
        val response = restTemplate.postForEntity("/games/$gameNumber/actions", ActionRequest("CHOOSE_WONDER", name), String::class.java)
        println(response)
    }

    fun availableWonderName(): String {
        val game = restTemplate.getForEntity("/games/$gameNumber", String::class.java)
        val root = objectMapper.readTree(game.body)
        val availableWonders = root.path("wonders")
        return availableWonders.first().get("name").asText()
    }
}

data class ActionRequest(val type: String, val name: String)