package com.moscichowski.wonders.builder

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.moscichowski.wonders.model.*
import java.io.File
import java.nio.file.Paths


class CardBuilder {
    fun getCards(): List<Card> {
        val path = this.javaClass.classLoader.getResource("cards.json").path
        val resource = this.javaClass.classLoader.getResource("cards.json")
        val json = resource.openStream().bufferedReader().use { it.readText() }
//        val json = File(path).readText(Charsets.UTF_8)
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(CardJsonModule())
        val readValue: List<List<Card>> = objectMapper.readValue(json, object : TypeReference<List<List<Card>>>() {})
        return readValue.first()
    }
}

fun main(args: Array<String>) {
    val path = Paths.get("").toAbsolutePath().toString()
    println("Working Directory = $path")
    File(".").list().forEach { println(it) }
    val fileName = "wonders-builder/src/main/resources/cards.json"

    val lines: List<String> = File(fileName).readLines()

    lines.forEach { line -> println(line) }
}