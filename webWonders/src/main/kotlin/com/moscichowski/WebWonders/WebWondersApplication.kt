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
import com.amazonaws.regions.Regions
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.AmazonS3
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import java.io.File
import java.util.*


@SpringBootApplication
class WebWondersApplication

fun getProperties(bucketName: String): Properties {

    val s3client = AmazonS3ClientBuilder
            .standard()
            .withRegion(Regions.US_EAST_1)
            .build()

    val s3Object = s3client.getObject(bucketName, "application.properties")
    val properties = Properties()
    properties.load(s3Object.objectContent)
    println("properties = $properties")
    return properties
}

fun main(args: Array<String>) {
    var builder = SpringApplicationBuilder().sources(WebWondersApplication::class.java)
    if (args.count() > 0) {
        args.forEach {
            if (it.contains("--s3config=")) {
                val s3 = it.split("=")[1]
                builder = builder.properties(getProperties(s3))
            }
        }
    }
    builder.run(*args)
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